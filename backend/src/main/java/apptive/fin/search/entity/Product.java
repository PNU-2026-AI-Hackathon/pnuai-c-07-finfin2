package apptive.fin.search.entity;

import apptive.fin.global.entity.BaseTimeEntity;
import apptive.fin.search.enums.ProductType;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    private String productCode;

    @Column(nullable = false)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String contentSummary;

    @Column(columnDefinition = "TEXT")
    private String joinMethod;

    @Column(columnDefinition = "TEXT")
    private String eligibilityText;

    @Column(columnDefinition = "TEXT")
    private String cautionText;

    @Column(columnDefinition = "TEXT")
    private String recruitmentPeriod;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductProperty> properties = new ArrayList<>();

    // 정부(온통청년) 상품 여부.
    public boolean isGovernment() {
        return source != null && ProductSource.GOVERNMENT_CODE.equals(source.getCode());
    }

    // 은행(FSS 공시) 상품 여부.
    public boolean isBank() {
        return source != null && ProductSource.BANK_CODE.equals(source.getCode());
    }
}
