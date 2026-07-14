package apptive.fin.apicollector.product.entity;

import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.normalize.dto.RequiredKeywordDraft;
import apptive.fin.apicollector.product.ContributionType;
import apptive.fin.apicollector.product.InterestRateType;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ReserveType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "productProperty", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductKeyword> keywords = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "productProperty", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductRequiredKeyword> requiredKeywords = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "productProperty", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductPreferentialRate> preferentialRates = new ArrayList<>();

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

    private ProductProperty(
            Product product,
            Provider provider,
            ProductPropertyDraft propertyDraft
    ) {
        this.product = product;
        applyDraft(provider, propertyDraft);
    }

    public static ProductProperty create(
            Product product,
            Provider provider,
            ProductPropertyDraft propertyDraft
    ) {
        return new ProductProperty(product, provider, propertyDraft);
    }

    /**
     * 기존 property를 id 유지한 채 draft 값으로 갱신한다(upsert의 update 경로).
     * 자연키(provider, intrRateType, reserveType, saveTrm)로 매칭된 property에만 호출된다.
     */
    public void updateFrom(Provider provider, ProductPropertyDraft propertyDraft) {
        applyDraft(provider, propertyDraft);
    }

    private void applyDraft(Provider provider, ProductPropertyDraft propertyDraft) {
        this.provider = provider;
        this.baseRate = propertyDraft.baseRate();
        this.maxRate = propertyDraft.maxRate();
        this.govContributionRate = propertyDraft.govContributionRate();
        this.govContributionType = propertyDraft.govContributionType();
        this.govMatchingRatio = propertyDraft.govMatchingRatio();
        this.govMonthlyFixedContribution = propertyDraft.govMonthlyFixedContribution();
        this.govContributionPeriodMonths = propertyDraft.govContributionPeriodMonths();
        this.excludeFromRateComparison = propertyDraft.excludeFromRateComparison();
        this.minMonthlyLimit = propertyDraft.minMonthlyLimit();
        this.maxMonthlyLimit = propertyDraft.maxMonthlyLimit();
        this.minAge = propertyDraft.minAge();
        this.maxAge = propertyDraft.maxAge();
        this.allowsMilitaryAgeExtension = propertyDraft.allowsMilitaryAgeExtension();
        this.militaryMaxAge = propertyDraft.militaryMaxAge();
        this.earnMaxAmt = propertyDraft.earnMaxAmt();
        this.earnPercent = propertyDraft.earnPercent();
        this.minTenureMonths = propertyDraft.minTenureMonths();
        this.requiresHomeless = propertyDraft.requiresHomeless();
        this.requiresHouseholder = propertyDraft.requiresHouseholder();
        this.isJoinable = true;
        this.applyUrl = propertyDraft.applyUrl();
        this.intrRateType = InterestRateType.fromCode(propertyDraft.intrRateType());
        this.reserveType = ReserveType.fromApiCode(propertyDraft.reserveType());
        this.saveTrm = propertyDraft.saveTerm();
        replaceKeywords(propertyDraft.keywords());
        replaceRequiredKeywords(propertyDraft.requiredKeywords());
        replacePreferentialRates(propertyDraft.preferentialRates());
    }

    public void replaceKeywords(List<KeywordValueEnum> keywordCodes) {
        Set<KeywordValueEnum> desiredKeywords = keywordCodes == null || keywordCodes.isEmpty()
                ? EnumSet.noneOf(KeywordValueEnum.class)
                : EnumSet.copyOf(keywordCodes);

        this.keywords.removeIf(keyword -> !desiredKeywords.contains(keyword.getKeywordCode()));

        Set<KeywordValueEnum> currentKeywords = new HashSet<>();
        for (ProductKeyword keyword : this.keywords) {
            currentKeywords.add(keyword.getKeywordCode());
        }

        for (KeywordValueEnum keywordCode : desiredKeywords) {
            if (!currentKeywords.contains(keywordCode)) {
                this.keywords.add(ProductKeyword.create(this, keywordCode));
            }
        }
    }

    public void replaceRequiredKeywords(List<RequiredKeywordDraft> drafts) {
        this.requiredKeywords.clear();
        if (drafts == null) {
            return;
        }

        for (RequiredKeywordDraft draft : drafts) {
            if (draft.keywordCode() != null) {
                this.requiredKeywords.add(ProductRequiredKeyword.create(this, draft));
            }
        }
    }

    public void replacePreferentialRates(List<PreferentialRateDraft> drafts) {
        this.preferentialRates.clear();
        if (drafts == null) {
            return;
        }

        for (PreferentialRateDraft draft : drafts) {
            if (draft.keywordCode() != null && draft.rate() != null) {
                this.preferentialRates.add(ProductPreferentialRate.create(this, draft));
            }
        }
    }

    public void markUnjoinable() {
        this.isJoinable = false;
    }

    public void markJoinable() {
        this.isJoinable = true;
    }
}
