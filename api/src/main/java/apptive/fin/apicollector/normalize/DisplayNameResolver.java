package apptive.fin.apicollector.normalize;

import apptive.fin.apicollector.Source;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 상품 집합 전체를 보고 디스플레이 이름(product_name)을 결정한다.
 * FSS 상품은 이름 끝 괄호를 제거하되, 제거 결과가 다른 상품과 겹치면 원본(괄호)을 유지한다.
 */
@Component
public class DisplayNameResolver {

    // 금감원 fin_prdt_nm은 "○○적금(정액적립식)", "○○예금(시즌2)"처럼 적립·지급 방식이나
    // 상품유형·시즌 표기를 이름 끝 괄호로 붙여 내려준다. PO 기준(00적금/00예금)에 따라 끝 괄호는 통째로 뗀다.
    // 더(The), 헤이(Hey)처럼 이름 중간에 박힌 브랜드 괄호는 끝이 아니라 그대로 남는다.
    private static final Pattern TRAILING_PAREN = Pattern.compile("\\s*\\([^()]*\\)$");

    public record Item(Long id, Source source, String originalName) {
    }

    /**
     * 각 상품 id별 최종 디스플레이 이름을 반환한다.
     * 규칙: FSS는 끝 괄호를 뗀 base를, 그 외는 원본을 base로 삼는다. base가 같은 상품끼리 묶어
     * 서로 다른 원본이 2개 이상이면(=괄호를 떼면 겹치면) 괄호가 실제로 떨어진 상품은 원본으로 되돌린다.
     */
    public Map<Long, String> resolve(Collection<Item> items) {
        Map<Long, String> baseById = new LinkedHashMap<>();
        Map<String, Set<String>> distinctOriginalsByBase = new HashMap<>();
        for (Item item : items) {
            String base = baseName(item);
            baseById.put(item.id(), base);
            distinctOriginalsByBase
                    .computeIfAbsent(base, key -> new HashSet<>())
                    .add(item.originalName());
        }

        Map<Long, String> displayById = new LinkedHashMap<>();
        for (Item item : items) {
            String base = baseById.get(item.id());
            boolean collides = distinctOriginalsByBase.get(base).size() >= 2;
            boolean parenWasStripped = !Objects.equals(item.originalName(), base);
            displayById.put(item.id(), collides && parenWasStripped ? item.originalName() : base);
        }
        return displayById;
    }

    private static String baseName(Item item) {
        if (item.source() == Source.FSS) {
            return stripTrailingParen(item.originalName());
        }
        return item.originalName();
    }

    private static String stripTrailingParen(String name) {
        if (name == null) {
            return null;
        }
        // $ 앵커라 매칭되는 괄호는 항상 맨 끝 하나뿐. "헤이(Hey)적금 (자유적립식)"은
        // 마지막 "(자유적립식)"만 지워지고 중간 "(Hey)"는 남는다.
        return TRAILING_PAREN.matcher(name).replaceFirst("").stripTrailing();
    }
}
