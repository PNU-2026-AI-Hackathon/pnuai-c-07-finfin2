package apptive.fin.apicollector.product.entity;

import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_preferential_rates")
public class ProductPreferentialRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_property_id", nullable = false)
    private ProductProperty productProperty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeywordValueEnum keywordCode;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal rate;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer minAge;
    private Integer maxAge;

    private ProductPreferentialRate(ProductProperty productProperty, PreferentialRateDraft draft) {
        this.productProperty = productProperty;
        this.keywordCode = draft.keywordCode();
        this.rate = draft.rate();
        this.description = draft.description();
        this.minAge = draft.minAge();
        this.maxAge = draft.maxAge();
    }

    public static ProductPreferentialRate create(ProductProperty productProperty, PreferentialRateDraft draft) {
        return new ProductPreferentialRate(productProperty, draft);
    }
}
