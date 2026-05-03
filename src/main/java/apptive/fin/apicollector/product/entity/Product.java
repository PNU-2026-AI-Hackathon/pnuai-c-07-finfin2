package apptive.fin.apicollector.product.entity;

import apptive.fin.apicollector.global.entity.BaseTimeEntity;
import apptive.fin.apicollector.normalize.ProductDraft;
import apptive.fin.apicollector.normalize.ProductOptionDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ProductType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "product",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_source_product_code",
                        columnNames = {"source_id", "product_code"}
                )
        }
)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private ProductSource source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    @Column(name = "product_code", nullable = false)
    private String productCode;

    @Column(nullable = false)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 공통
    @Column(precision = 5, scale = 2)
    private BigDecimal baseRate;

    @Column(precision = 5, scale = 2)
    private BigDecimal maxRate;

    private Long minMonthlyLimit;
    private Long maxMonthlyLimit;

    // 조건 (파싱 결과)
    private Integer minAge;
    private Integer maxAge;
    private Long    earnMaxAmt;
    private Integer earnPercent;
    private Integer minTenureMonths;

    @Column(nullable = false)
    private Boolean requiresHomeless    = false;

    @Column(nullable = false)
    private Boolean requiresHouseholder = false;

    // url
    private String applyUrl;

    // 연관관계
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductKeyword> keywords = new ArrayList<>();

    private Product(
            ProductSource source,
            Provider provider,
            ProductType type,
            String productCode,
            String productName
    ) {
        this.source = source;
        this.provider = provider;
        this.type = type;
        this.productCode = productCode;
        this.productName = productName;
    }

    public static Product create(
            ProductSource source,
            Provider provider,
            ProductType type,
            String productCode,
            String productName
    ) {
        return new Product(source, provider, type, productCode, productName);
    }

    public void updateFrom(ProductDraft draft, Provider provider) {
        this.provider = provider;
        this.type = draft.type();
        this.productName = draft.productName();
        this.content = draft.content();
        this.baseRate = draft.baseRate();
        this.maxRate = draft.maxRate();
        this.minMonthlyLimit = draft.minMonthlyLimit();
        this.maxMonthlyLimit = draft.maxMonthlyLimit();
        this.minAge = draft.minAge();
        this.maxAge = draft.maxAge();
        this.earnMaxAmt = draft.earnMaxAmt();
        this.earnPercent = draft.earnPercent();
        this.minTenureMonths = draft.minTenureMonths();
        this.requiresHomeless = draft.requiresHomeless();
        this.requiresHouseholder = draft.requiresHouseholder();
        this.applyUrl = draft.applyUrl();
    }

    public void replaceOptions(List<ProductOptionDraft> optionDrafts) {
        this.options.clear();
        for (ProductOptionDraft optionDraft : optionDrafts) {
            this.options.add(ProductOption.create(this, optionDraft));
        }
    }

    public void replaceKeywords(List<KeywordValueEnum> keywordCodes) {
        Set<KeywordValueEnum> desiredKeywords = keywordCodes == null || keywordCodes.isEmpty()
                ? EnumSet.noneOf(KeywordValueEnum.class)
                : EnumSet.copyOf(keywordCodes);

        this.keywords.removeIf(keyword -> !desiredKeywords.contains(keyword.getKeywordCode()));

        Set<KeywordValueEnum> currentKeywords = new HashSet<>();
        for (ProductKeyword keyword : this.keywords) {
            currentKeywords.add(keyword.getKeywordCode());
        }

        for (KeywordValueEnum keywordCode : desiredKeywords) {
            if (!currentKeywords.contains(keywordCode)) {
                this.keywords.add(ProductKeyword.create(this, keywordCode));
            }
        }
    }
}
