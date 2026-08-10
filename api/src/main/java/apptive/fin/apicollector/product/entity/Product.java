package apptive.fin.apicollector.product.entity;

import apptive.fin.apicollector.global.entity.BaseTimeEntity;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.InterestRateType;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.product.ReserveType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    private String productCode;

    @Column(nullable = false)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String contentSummary;

    @Column(columnDefinition = "TEXT")
    private String joinMethod;

    @Column(columnDefinition = "TEXT")
    private String eligibilityText;

    @Column(columnDefinition = "TEXT")
    private String cautionText;

    @Column(columnDefinition = "TEXT")
    private String recruitmentPeriod;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductProperty> properties = new ArrayList<>();

    private Product(
            ProductSource source,
            ProductType type,
            String productCode,
            String productName
    ) {
        this.source = source;
        this.type = type;
        this.productCode = productCode;
        this.productName = productName;
    }

    public static Product create(
            ProductSource source,
            ProductType type,
            String productCode,
            String productName
    ) {
        return new Product(source, type, productCode, productName);
    }

    public void updateFrom(ProductDraft draft) {
        this.type = draft.type();
        this.productName = draft.productName();
        this.content = draft.content();
        this.contentSummary = draft.contentSummary();
        this.joinMethod = draft.joinMethod();
        this.eligibilityText = draft.eligibilityText();
        this.cautionText = draft.cautionText();
        this.recruitmentPeriod = draft.recruitmentPeriod();
    }

    /**
     * draft 목록과 기존 property를 자연키로 재조정(upsert)한다.
     * - 키가 일치하는 기존 property는 id를 유지한 채 갱신(update-in-place)
     * - 새 키는 새 property로 추가(insert)
     * - draft에서 사라진 키는 물리 삭제하지 않고 {@code isJoinable=false}로 소프트 비활성화
     *
     * 사라진 상품 전체를 소프트 비활성화하는 배치 흐름과 철학을 맞추고, backend의
     * list↔detail productPropertyId 계약을 안정적으로 유지하기 위함이다.
     */
    public void replaceProperties(
            List<ProductPropertyDraft> propertyDrafts,
            Function<ProductPropertyDraft, Provider> providerResolver
    ) {
        replaceProperties(propertyDrafts, providerResolver, false);
    }

    public void replaceProperties(
            List<ProductPropertyDraft> propertyDrafts,
            Function<ProductPropertyDraft, Provider> providerResolver,
            boolean preserveExistingApplyUrl
    ) {
        Map<PropertyKey, ProductProperty> existingByKey = new HashMap<>();
        for (ProductProperty property : this.properties) {
            existingByKey.putIfAbsent(keyOf(property), property);
        }

        Set<PropertyKey> seenKeys = new HashSet<>();
        for (ProductPropertyDraft propertyDraft : propertyDrafts) {
            Provider provider = providerResolver.apply(propertyDraft);
            PropertyKey key = keyOf(propertyDraft);
            // draft에 등장한 모든 키를 seenKeys에 등록해야, 아래 소프트 비활성화 루프가
            // 'draft에서 사라진 키'만 끈다. (등록을 update 분기에만 하면 신규 insert된 property가
            // 곧바로 markUnjoinable 되어 최초 동기화 시 전부 isJoinable=false가 되는 버그)
            boolean firstOccurrence = seenKeys.add(key);
            ProductProperty existing = existingByKey.get(key);

            if (existing != null && firstOccurrence) {
                existing.updateFrom(provider, propertyDraft, preserveExistingApplyUrl);
            } else {
                this.properties.add(ProductProperty.create(this, provider, propertyDraft));
            }
        }

        for (ProductProperty property : this.properties) {
            if (!seenKeys.contains(keyOf(property))) {
                property.markUnjoinable();
            }
        }
    }

    private static PropertyKey keyOf(ProductProperty property) {
        return new PropertyKey(
                property.getProvider() == null ? null : property.getProvider().getCode(),
                property.getIntrRateType(),
                property.getReserveType(),
                property.getSaveTrm(),
                property.getVariantCode()
        );
    }

    private static PropertyKey keyOf(ProductPropertyDraft draft) {
        return new PropertyKey(
                draft.providerCode(),
                InterestRateType.fromCode(draft.intrRateType()),
                ReserveType.fromApiCode(draft.reserveType()),
                draft.saveTerm(),
                draft.variantCode()
        );
    }

    /**
     * 한 상품 내에서 ProductProperty를 유일하게 식별하는 자연키.
     * draft/entity 양쪽에서 동일한 enum 변환을 거쳐 계산해야 매칭이 어긋나지 않는다.
     */
    private record PropertyKey(
            String providerCode,
            InterestRateType intrRateType,
            ReserveType reserveType,
            Integer saveTrm,
            String variantCode
    ) {
    }

    public void markUnjoinable() {
        this.properties.forEach(ProductProperty::markUnjoinable);
    }

    public void markJoinable() {
        this.properties.forEach(ProductProperty::markJoinable);
    }
}
