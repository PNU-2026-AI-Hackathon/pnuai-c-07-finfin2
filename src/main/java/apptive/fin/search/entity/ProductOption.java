package apptive.fin.search.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name="product_option")
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String intrRateType;

    private String intrRateTypeNm;

    @Column(nullable = false)
    private Integer saveTrm;

    @Column(precision = 5, scale = 2)
    private BigDecimal intrRate;

    @Column(precision = 5, scale = 2)
    private BigDecimal intrRate2;
}
