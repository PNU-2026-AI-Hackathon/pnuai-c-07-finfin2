package apptive.fin.search.entity;

import apptive.fin.provider.entity.Provider;
import apptive.fin.search.enums.ContributionType;
import apptive.fin.search.enums.ExtractionConfidence;
import apptive.fin.search.enums.InterestRateType;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.enums.RequiredKeywordEffect;
import apptive.fin.search.enums.ReserveType;
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
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    @Enumerated(EnumType.STRING)
    private ContributionType govContributionType;

    @Column(precision = 8, scale = 4)
    private BigDecimal govMatchingRatio;

    private Long govMonthlyFixedContribution;
    private Integer govContributionPeriodMonths;

    @Column(nullable = false)
    private Boolean excludeFromRateComparison = false;

    private Long minMonthlyLimit;
    private Long maxMonthlyLimit;

    private Integer minAge;
    private Integer maxAge;

    @Column(nullable = false)
    private Boolean allowsMilitaryAgeExtension = false;

    private Integer militaryMaxAge;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "rsrv_type")
    private ReserveType reserveType;

    private Integer saveTrm;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "productProperty", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductKeyword> keywords = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "productProperty", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductRequiredKeyword> requiredKeywords = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "productProperty", fetch = FetchType.LAZY)
    private List<ProductPreferentialRate> preferentialRates = new ArrayList<>();

    // 제공자(은행/기관) 이름. provider 미설정 시 null.
    public String providerName() {
        return provider != null ? provider.getName() : null;
    }

    public boolean isJoinable() {
        return Boolean.TRUE.equals(isJoinable);
    }

    // 이 property의 provider 코드가 주어진 코드 목록에 포함되는지(별칭 아닌 코드 기준).
    public boolean matchesAnyProvider(List<String> providerCodes) {
        if (provider == null || providerCodes == null) {
            return false;
        }
        String code = provider.getCode();
        return providerCodes.stream().anyMatch(c -> c != null && c.equals(code));
    }

    /**
     * 이 property의 전체 키워드.
     * 순수 태그, BANK_* 우대금리, REQUIRE/HIGH인 STATUS_* 가입조건의 합집합이다.
     */
    public Set<KeywordValueEnum> keywordCodes() {
        EnumSet<KeywordValueEnum> codes = EnumSet.noneOf(KeywordValueEnum.class);
        keywords.stream()
                .map(ProductKeyword::getKeywordCode)
                .filter(Objects::nonNull)
                .filter(code -> !code.isPreferentialRate())
                .filter(code -> !code.isRequired())
                .forEach(codes::add);
        preferentialRates.stream()
                .map(ProductPreferentialRate::getKeywordCode)
                .filter(Objects::nonNull)
                .filter(KeywordValueEnum::isPreferentialRate)
                .forEach(codes::add);
        requiredKeywords.stream()
                .filter(required -> required.getEffect() == RequiredKeywordEffect.REQUIRE)
                .filter(required -> required.getConfidence() == ExtractionConfidence.HIGH)
                .map(ProductRequiredKeyword::getKeywordCode)
                .filter(Objects::nonNull)
                .filter(KeywordValueEnum::isRequired)
                .forEach(codes::add);
        return Collections.unmodifiableSet(codes);
    }
}
