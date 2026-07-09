package apptive.fin.search.repository;

import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // product_code 단건 조회 + properties/provider 즉시 로딩 (지연 로딩 예외 방지)
    @Query("""
            SELECT DISTINCT p FROM Product p
            JOIN FETCH p.source
            JOIN FETCH p.properties pp
            LEFT JOIN FETCH pp.provider
            WHERE p.productCode = :productCode
            """)
    Optional<Product> findByProductCodeWithProperties(@Param("productCode") String productCode);

    // 조건 조회
    @Query("""
            SELECT DISTINCT p FROM Product p
            JOIN FETCH p.properties pp
            WHERE pp.isJoinable = TRUE
                AND (:age IS NULL OR pp.minAge IS NULL OR pp.minAge <= :age)
                AND (
                    :age IS NULL
                    OR pp.maxAge IS NULL
                    OR pp.maxAge >= :age
                    OR (
                        :militaryAgeExtensionRequested = TRUE
                        AND :age BETWEEN 35 AND 39
                        AND pp.allowsMilitaryAgeExtension = TRUE
                        AND COALESCE(pp.militaryMaxAge, 39) >= :age
                    )
                )
                AND (:incomeProofUnavailable = FALSE OR (pp.earnMaxAmt IS NULL AND pp.earnPercent IS NULL))
                AND (:incomeProofUnavailable = TRUE OR :annualIncome IS NULL OR pp.earnMaxAmt IS NULL OR pp.earnMaxAmt >= :annualIncome)
                AND (:householdIncomePercent IS NULL OR pp.earnPercent IS NULL OR pp.earnPercent >= :householdIncomePercent)
                AND (:isHomeless IS NULL OR pp.requiresHomeless = FALSE OR :isHomeless = TRUE)
                AND (:isHouseholder IS NULL OR pp.requiresHouseholder = FALSE OR :isHouseholder = TRUE)
                AND (:tenureMonths IS NULL OR pp.minTenureMonths IS NULL OR pp.minTenureMonths <= :tenureMonths)
                AND (:monthlyDeposit IS NULL OR pp.minMonthlyLimit IS NULL OR pp.minMonthlyLimit <= :monthlyDeposit)
            """)
    List<Product> findEligibleProducts(
            @Param("age") Integer age,
            @Param("annualIncome") Long annualIncome,
            @Param("householdIncomePercent") Integer householdIncomePercent,
            @Param("incomeProofUnavailable") Boolean incomeProofUnavailable,
            @Param("militaryAgeExtensionRequested") Boolean militaryAgeExtensionRequested,
            @Param("isHomeless") Boolean isHomeless,
            @Param("isHouseholder") Boolean isHouseholder,
            @Param("tenureMonths") Integer tenureMonths,
            @Param("monthlyDeposit") Long monthlyDeposit
    );

    // 키워드 기준 조회
    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN FETCH p.properties pp
        JOIN pp.keywords k
        WHERE k.keywordCode IN :keywords
    """)
    List<Product> findByKeywords(@Param("keywords") List<KeywordValueEnum> keywords);

    // 상품명 검색
    @Query("""
           SELECT DISTINCT p FROM Product p
           LEFT JOIN FETCH p.properties pp
           WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%',:searchInput,'%'))
           AND pp.isJoinable = TRUE
    """)
    List<Product> findByProductNameContaining(@Param("searchInput") String searchInput);

}
