package apptive.fin.search.entity;

import apptive.fin.search.enums.ExtractionConfidence;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.enums.RequiredKeywordEffect;
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

@Entity
@Getter
@Table(name = "product_property_required_keyword")
public class ProductRequiredKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_property_id", nullable = false)
    private ProductProperty productProperty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeywordValueEnum keywordCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequiredKeywordEffect effect = RequiredKeywordEffect.REQUIRE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExtractionConfidence confidence = ExtractionConfidence.HIGH;
}
