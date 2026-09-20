package apptive.fin.apicollector.product.entity;

import apptive.fin.apicollector.normalize.dto.RequiredKeywordDraft;
import apptive.fin.apicollector.product.ExtractionConfidence;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.RequiredKeywordEffect;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_property_required_keyword")
public class ProductRequiredKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_property_id", nullable = false)
    private ProductProperty productProperty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeywordValueEnum keywordCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequiredKeywordEffect effect = RequiredKeywordEffect.REQUIRE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExtractionConfidence confidence = ExtractionConfidence.HIGH;

    private ProductRequiredKeyword(ProductProperty productProperty, RequiredKeywordDraft draft) {
        this.productProperty = productProperty;
        this.keywordCode = draft.keywordCode();
        this.effect = draft.effect();
        this.confidence = draft.confidence();
    }

    public static ProductRequiredKeyword create(ProductProperty productProperty, RequiredKeywordDraft draft) {
        return new ProductRequiredKeyword(productProperty, draft);
    }
}
