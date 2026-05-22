package apptive.fin.apicollector.product.entity;

import apptive.fin.apicollector.global.entity.BaseTimeEntity;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.ProductType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
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
    }

    public void replaceProperties(
            List<ProductPropertyDraft> propertyDrafts,
            Function<ProductPropertyDraft, Provider> providerResolver
    ) {
        this.properties.clear();
        propertyDrafts.forEach(propertyDraft ->
                this.properties.add(ProductProperty.create(this, providerResolver.apply(propertyDraft), propertyDraft))
        );
    }

    public void markUnjoinable() {
        this.properties.forEach(ProductProperty::markUnjoinable);
    }

    public void markJoinable() {
        this.properties.forEach(ProductProperty::markJoinable);
    }
}
