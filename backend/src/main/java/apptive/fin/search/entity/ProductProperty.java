package apptive.fin.search.entity;

import apptive.fin.search.InterestRateType;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "product_properties")
public class ProductProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @Column(precision = 5, scale = 2)
    private BigDecimal baseRate;

    @Column(precision = 5, scale = 2)
    private BigDecimal maxRate;

    @Column(precision = 5, scale = 2)
    private BigDecimal govContributionRate;

    private Long minMonthlyLimit;
    private Long maxMonthlyLimit;

    private Integer minAge;
    private Integer maxAge;
    private Long earnMaxAmt;
    private Integer earnPercent;
    private Integer minTenureMonths;

    @Column(nullable = false)
    private Boolean requiresHomeless = false;

    @Column(nullable = false)
    private Boolean requiresHouseholder = false;

    @Column(nullable = false)
    private Boolean isJoinable = true;

    private String applyUrl;

    @Enumerated(EnumType.STRING)
    private InterestRateType intrRateType;

    private Integer saveTrm;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "productProperty", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductKeyword> keywords = new ArrayList<>();
}
