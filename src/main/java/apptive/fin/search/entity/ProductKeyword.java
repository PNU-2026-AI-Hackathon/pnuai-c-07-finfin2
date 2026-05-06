package apptive.fin.search.entity;

import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.ProductType;
import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
@Table(name = "product_keyword")
public class ProductKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    private KeywordValueEnum keywordCode;
}
