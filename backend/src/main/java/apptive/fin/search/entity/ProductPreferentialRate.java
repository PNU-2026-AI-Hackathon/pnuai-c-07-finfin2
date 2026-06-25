package apptive.fin.search.entity;

import apptive.fin.search.KeywordValueEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
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
}
