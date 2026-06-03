package apptive.fin.search.entity;

import apptive.fin.search.KeywordValueEnum;
import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
@Table(name = "product_property_keyword")
public class ProductKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_property_id")
    private ProductProperty productProperty;

    @Enumerated(EnumType.STRING)
    private KeywordValueEnum keywordCode;
}
