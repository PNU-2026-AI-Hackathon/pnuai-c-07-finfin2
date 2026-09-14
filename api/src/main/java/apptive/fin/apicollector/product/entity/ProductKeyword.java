package apptive.fin.apicollector.product.entity;

import apptive.fin.apicollector.product.KeywordValueEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "product_property_keyword",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_property_keyword_property_keyword_code",
                        columnNames = {"product_property_id", "keyword_code"}
                )
        }
)
public class ProductKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_property_id", nullable = false)
    private ProductProperty productProperty;

    @Enumerated(EnumType.STRING)
    @Column(name = "keyword_code", nullable = false)
    private KeywordValueEnum keywordCode;

    private ProductKeyword(ProductProperty productProperty, KeywordValueEnum keywordCode) {
        this.productProperty = productProperty;
        this.keywordCode = keywordCode;
    }

    public static ProductKeyword create(ProductProperty productProperty, KeywordValueEnum keywordCode) {
        return new ProductKeyword(productProperty, keywordCode);
    }
}
