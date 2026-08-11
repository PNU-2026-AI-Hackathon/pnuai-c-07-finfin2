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
import java.util.regex.Pattern;

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

    // 금감원 fin_prdt_nm은 "○○적금\n(정액적립식)", "○○통장(정기예금)", "○○예금(시즌2)"처럼
    // 적립·지급 방식이나 상품유형·시즌 표기를 이름 끝 괄호로 붙여 내려준다.
    // 수집기는 이 괄호를 원천 그대로 저장한다(은행 URL 스크래퍼가 상품을 구분하는 근거라 지우면 안 된다).
    // PO 기준(00적금/00예금)에 맞춘 이름은 응답을 만드는 이 시점에 만든다.
    public String getDisplayProductName() {
        return stripTrailingParen(productName);
    }

    private static final Pattern TRAILING_PAREN = Pattern.compile("\\s*\\([^()]*\\)$");

    // $ 앵커라 매칭되는 괄호는 항상 맨 끝 하나뿐. "헤이(Hey)적금 (자유적립식)"은
    // 마지막 "(자유적립식)"만 지워지고 중간 "(Hey)"는 남는다.
    static String stripTrailingParen(String name) {
        if (name == null) {
            return null;
        }
        return TRAILING_PAREN.matcher(name).replaceFirst("").stripTrailing();
    }
}
