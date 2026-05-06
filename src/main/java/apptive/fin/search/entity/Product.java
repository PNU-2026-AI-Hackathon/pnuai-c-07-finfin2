// search/entity/Product.java
package apptive.fin.search.entity;

import apptive.fin.global.entity.BaseTimeEntity;
import apptive.fin.search.ProductType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "product")
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private ProductSource source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    private String productCode;

    @Column(nullable = false)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 공통
    @Column(precision = 5, scale = 2)
    private BigDecimal baseRate;

    @Column(precision = 5, scale = 2)
    private BigDecimal maxRate;

    private Long minMonthlyLimit;
    private Long maxMonthlyLimit;

    // 조건 (파싱 결과)
    private Integer minAge;
    private Integer maxAge;
    private Long    earnMaxAmt;
    private Integer earnPercent;
    private Integer minTenureMonths;

    @Column(nullable = false)
    private Boolean requiresHomeless    = false;

    @Column(nullable = false)
    private Boolean requiresHouseholder = false;

    // url
    private String applyUrl;

    // 연관관계
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductOption> options = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductKeyword> keywords = new ArrayList<>();

    // 현재 가입 가능 상품 판단
    @Column(nullable = false)
    private Boolean isJoinable = true;
}