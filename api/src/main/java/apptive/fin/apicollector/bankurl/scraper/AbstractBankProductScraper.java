package apptive.fin.apicollector.bankurl.scraper;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractBankProductScraper implements BankProductScraper {

    private static final List<String> PRODUCT_WORDS = List.of("예금", "적금", "통장", "deposit", "saving");
    private static final List<String> NON_PRODUCT_WORDS = List.of(
            "상품공시", "보호금융", "약관", "설명서", "금리보기", "미리보기",
            "상담하기", "인터넷가입", "영업점가입", "상세보기", "상품담기", "비교함", "본문", "메뉴"
    );

    protected final ProductNameSimilarity similarity = new ProductNameSimilarity();

    @Override
    public ScrapedProduct scrape(Browser browser, String productName, int timeoutMillis) {
        try (BrowserContext context = browser.newContext()) {
            context.setDefaultTimeout(timeoutMillis);
            context.setDefaultNavigationTimeout(timeoutMillis);
            List<ProductCandidate> candidates = search(context, productName);
            ProductCandidate selected = select(candidates, productName);
            return collect(context, selected, productName);
        }
    }

    protected abstract List<ProductCandidate> search(BrowserContext context, String productName);

    protected List<ProductCandidate> extractProducts(Document document, String currentUrl) {
        List<ProductCandidate> candidates = new ArrayList<>(document.select("a").stream()
                .map(anchor -> new ProductCandidate(
                        cleanText(anchor.text()),
                        urlFromAnchor(anchor, currentUrl)
                ))
                .filter(candidate -> looksLikeProductName(candidate.name()))
                .filter(candidate -> !candidate.url().isBlank())
                .toList());
        candidates.addAll(extractProductBlocks(document, currentUrl));
        return dedupe(candidates);
    }

    private List<ProductCandidate> extractProductBlocks(Document document, String currentUrl) {
        List<ProductCandidate> candidates = new ArrayList<>();
        String selectors = String.join(",", List.of(
                ".product_list li", ".product-list > li", ".product", ".prd-info",
                ".list-con-area", ".listTyProducts > li", ".goods-list li", "tr"
        ));
        int index = 0;
        for (Element block : document.select(selectors)) {
            index++;
            String name = bestProductBlockName(block);
            if (!looksLikeProductName(name)) {
                continue;
            }
            String url = "";
            for (Element anchor : block.select("a")) {
                url = urlFromAnchor(anchor, currentUrl);
                if (!url.isBlank()) {
                    break;
                }
            }
            if (url.isBlank()) {
                String blockId = cleanText(block.id());
                url = currentUrl.split("#", 2)[0] + "#"
                        + (blockId.isBlank() ? "product-" + index : blockId);
            }
            candidates.add(new ProductCandidate(name, url));
        }
        return candidates;
    }

    private String bestProductBlockName(Element block) {
        for (String selector : List.of(
                ".prdtName a", ".prdtName", ".name a", ".name", ".product_tit",
                ".tit a", ".tit", "dt.name a", "strong a", "h1", "h2", "h3", "h4"
        )) {
            Element target = block.selectFirst(selector);
            String text = cleanText(target == null ? "" : target.text());
            if (looksLikeProductName(text)) {
                return text;
            }
        }
        String text = cleanText(block.text()).replaceFirst(
                "(상세보기|인터넷가입|상담하기|금리보기|미리보기).*$", ""
        );
        Matcher matcher = Pattern.compile(
                "([A-Za-z0-9가-힣·★!+\\-() /]+(?:예금|적금|통장)[A-Za-z0-9가-힣·★!+\\-() /]*)"
        ).matcher(text);
        return cleanText(matcher.find() ? matcher.group(1) : text.substring(0, Math.min(text.length(), 80)));
    }

    protected List<ProductCandidate> extractProductsWithSelectors(
            Document document,
            String currentUrl,
            List<String> blockSelectors,
            List<String> nameSelectors,
            boolean includeAnchors
    ) {
        List<ProductCandidate> candidates = new ArrayList<>();
        if (includeAnchors) {
            candidates.addAll(extractProducts(document, currentUrl));
        }
        for (Element block : document.select(String.join(",", blockSelectors))) {
            String name = bestName(block, nameSelectors);
            if (!looksLikeProductName(name)) {
                continue;
            }
            for (Element anchor : block.select("a")) {
                String url = urlFromAnchor(anchor, currentUrl);
                if (!url.isBlank()) {
                    candidates.add(new ProductCandidate(name, url));
                    break;
                }
            }
        }
        return dedupe(candidates);
    }

    protected String bestName(Element block, List<String> selectors) {
        for (String selector : selectors) {
            Element target = block.selectFirst(selector);
            String text = cleanText(target == null ? "" : target.text());
            if (looksLikeProductName(text)) {
                return text;
            }
        }
        return cleanText(block.text());
    }

    protected ProductCandidate select(List<ProductCandidate> candidates, String productName) {
        return candidates.stream()
                .max((left, right) -> Double.compare(
                        similarity.score(left.name(), productName),
                        similarity.score(right.name(), productName)
                ))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No product URL candidates found for '" + productName + "'"
                ));
    }

    protected ScrapedProduct collect(
            BrowserContext context,
            ProductCandidate selected,
            String productName
    ) {
        try (Page page = context.newPage()) {
            navigate(page, selected.url());
            String title = pageTitle(page);
            if (title.isBlank()
                    || similarity.score(productName, selected.name()) > similarity.score(productName, title)) {
                title = selected.name();
            }
            return new ScrapedProduct(title, selected.url());
        }
    }

    protected List<ProductCandidate> searchPages(
            BrowserContext context,
            String productName,
            List<String> urlTemplates,
            BiFunction<Document, String, List<ProductCandidate>> extractor,
            boolean searchOnPage
    ) {
        List<ProductCandidate> candidates = new ArrayList<>();
        for (String template : urlTemplates) {
            String url = template.replace("{q}", URLEncoder.encode(productName, StandardCharsets.UTF_8));
            try (Page page = context.newPage()) {
                navigate(page, url);
                if (searchOnPage) {
                    trySearchOnPage(page, productName);
                    settle(page);
                }
                for (PageContent content : pageContents(page)) {
                    candidates.addAll(extractor.apply(
                            Jsoup.parse(content.html(), content.url()),
                            content.url()
                    ));
                }
            }
        }
        return dedupe(candidates);
    }

    protected void trySearchOnPage(Page page, String productName) {
        for (String selector : List.of(
                "input.ml25", "input[name=query]", "input[name=kwd]", "input[name=keyword]",
                "input[name=searchWord]", "input[type=search]", "#query", "#keyword", "#AKCKwd"
        )) {
            try {
                Locator input = page.locator(selector).first();
                if (input.count() == 0) {
                    continue;
                }
                input.fill(productName, new Locator.FillOptions().setTimeout(2_000));
                submitSearch(input);
                return;
            } catch (PlaywrightException ignored) {
                // Try the next known search input.
            }
        }
    }

    private void submitSearch(Locator input) {
        for (String selector : List.of(
                "xpath=following-sibling::a[contains(@class, 'btnTyBlue01')]",
                "xpath=following-sibling::button[contains(@class, 'btnTyBlue01')]",
                "xpath=ancestor::div[1]//a[contains(normalize-space(.), '검색')]",
                "xpath=ancestor::div[1]//button[contains(normalize-space(.), '검색')]",
                "xpath=ancestor::form[1]//button[@type='submit']",
                "xpath=ancestor::form[1]//input[@type='submit']"
        )) {
            try {
                Locator button = input.locator(selector).first();
                if (button.count() > 0) {
                    button.click(new Locator.ClickOptions().setTimeout(2_000));
                    return;
                }
            } catch (PlaywrightException ignored) {
                // Try the next known search button.
            }
        }
        input.press("Enter", new Locator.PressOptions().setTimeout(2_000));
    }

    protected void navigate(Page page, String url) {
        page.navigate(url, new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                .setTimeout(30_000));
        settle(page);
    }

    protected void settle(Page page) {
        try {
            page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(10_000));
        } catch (PlaywrightException ignored) {
            // Some bank pages keep long-lived network connections open.
        }
        if (settleMillis() > 0) {
            page.waitForTimeout(settleMillis());
        }
    }

    protected double settleMillis() {
        return 1_000;
    }

    protected String pageTitle(Page page) {
        for (String selector : titleSelectors()) {
            try {
                Locator locator = page.locator(selector).first();
                if (locator.count() == 0) {
                    continue;
                }
                String title = cleanText(locator.getAttribute("value"));
                if (title.isBlank()) {
                    title = cleanText(locator.getAttribute("data-product-name"));
                }
                if (title.isBlank()) {
                    title = cleanText(locator.innerText(new Locator.InnerTextOptions().setTimeout(2_000)));
                }
                if (looksLikeProductName(title)) {
                    return title;
                }
            } catch (PlaywrightException ignored) {
                // Try the next title selector.
            }
        }
        try {
            String openGraphTitle = cleanText(
                    page.locator("meta[property='og:title']").first().getAttribute("content")
            );
            if (!openGraphTitle.isBlank()) {
                return openGraphTitle;
            }
        } catch (PlaywrightException ignored) {
            // Fall back to the document title.
        }
        return cleanText(page.title()).split("\\s*[|>_<-]\\s*")[0];
    }

    protected List<String> titleSelectors() {
        return List.of(
                "input[name=PRD_NM]", "input[name=prdNm]", "input[name=prd_nm]",
                "input[name=productName]", "input[name=product_name]", "[data-product-name]",
                "h1", "h2", ".product-title", ".prd-title", ".tit", ".name"
        );
    }

    protected List<PageContent> pageContents(Page page) {
        List<PageContent> contents = new ArrayList<>();
        contents.add(new PageContent(page.content(), page.url()));
        for (Frame frame : page.frames()) {
            if (frame == page.mainFrame()) {
                continue;
            }
            try {
                contents.add(new PageContent(frame.content(), frame.url().isBlank() ? page.url() : frame.url()));
            } catch (PlaywrightException ignored) {
                // Detached frames are expected on SPA pages.
            }
        }
        return contents;
    }

    protected String cleanText(String value) {
        return value == null ? "" : value.replaceAll("\\s+", " ").trim();
    }

    protected boolean looksLikeProductName(String value) {
        String text = cleanText(value);
        if (text.isEmpty() || text.length() > 90 || isGenericProductName(text)) {
            return false;
        }
        String lowered = text.toLowerCase(Locale.ROOT);
        boolean hasProductWord = PRODUCT_WORDS.stream().anyMatch(lowered::contains);
        return hasProductWord && NON_PRODUCT_WORDS.stream().noneMatch(text::contains);
    }

    protected String urlFromAnchor(Element anchor, String currentUrl) {
        String href = cleanText(anchor.attr("href"));
        String onclick = cleanText(anchor.attr("onclick"));
        String javascriptUrl = urlFromJavascript(onclick, currentUrl);
        if (javascriptUrl.isBlank() && href.toLowerCase(Locale.ROOT).startsWith("javascript:")) {
            javascriptUrl = urlFromJavascript(href, currentUrl);
        }
        if (!javascriptUrl.isBlank()
                && (href.isBlank() || href.startsWith("#") || href.toLowerCase(Locale.ROOT).startsWith("javascript:"))) {
            return javascriptUrl;
        }
        if (!href.isBlank() && !href.startsWith("#") && !href.toLowerCase(Locale.ROOT).startsWith("javascript:")) {
            return absoluteUrl(href, currentUrl);
        }
        if (!javascriptUrl.isBlank()) {
            return javascriptUrl;
        }
        for (String attribute : List.of("data-url", "data-href")) {
            String value = cleanText(anchor.attr(attribute));
            if (!value.isBlank()) {
                return absoluteUrl(value, currentUrl);
            }
        }
        return "";
    }

    protected String absoluteUrl(String value, String currentUrl) {
        try {
            return URI.create(currentUrl).resolve(value).toString();
        } catch (IllegalArgumentException exception) {
            return "";
        }
    }

    protected List<ProductCandidate> dedupe(List<ProductCandidate> candidates) {
        Map<String, ProductCandidate> unique = new LinkedHashMap<>();
        for (ProductCandidate candidate : candidates) {
            String name = cleanText(candidate.name());
            String url = cleanText(candidate.url());
            if (!name.isBlank() && !url.isBlank()) {
                unique.putIfAbsent(name + "\u0000" + url, new ProductCandidate(name, url));
            }
        }
        return List.copyOf(unique.values());
    }

    protected List<String> queryVariants(String productName) {
        return List.of(
                        productName,
                        productName.replaceAll("\\([^)]*\\)", ""),
                        productName.replaceAll("\\[[^]]*]", ""),
                        productName.replaceAll("\\s+", "")
                ).stream()
                .map(this::cleanText)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();
    }

    private String urlFromJavascript(String script, String currentUrl) {
        if (script.isBlank()) {
            return "";
        }
        for (String regex : List.of("['\"](https?://[^'\"]+)['\"]", "['\"](/[^'\"]+)['\"]")) {
            Matcher matcher = Pattern.compile(regex).matcher(script);
            if (matcher.find()) {
                return absoluteUrl(matcher.group(1), currentUrl);
            }
        }
        Matcher imProduct = Pattern.compile("goProductDetailByPdCd\\(['\"]([^'\"]+)").matcher(script);
        if (imProduct.find()) {
            return absoluteUrl("/com_ebz_fpm_main.act?pd_cd=" + imProduct.group(1), currentUrl);
        }
        Matcher nhProduct = Pattern.compile("lfGetDt\\(['\"]([^'\"]+)['\"]").matcher(script);
        if (nhProduct.find()) {
            String code = nhProduct.group(1);
            return absoluteUrl(
                    "/servlet/BFDCW1021R.view?detailPsnFncWrsC=" + code
                            + "&psnFncWrsC=" + code
                            + "&listServiceId=BFDCW1011R",
                    currentUrl
            );
        }
        return "";
    }

    private boolean isGenericProductName(String value) {
        String normalized = value.replaceAll("[^0-9a-zA-Z가-힣]", "").toLowerCase(Locale.ROOT);
        return List.of("예금", "적금", "통장", "deposit", "saving", "savings").contains(normalized);
    }

    protected record PageContent(String html, String url) {
    }
}
