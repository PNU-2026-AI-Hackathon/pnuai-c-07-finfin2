package apptive.fin.search.repository;

import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 조건 조회
    @Query("""
            SELECT DISTINCT p FROM Product p
            JOIN FETCH p.properties pp
            WHERE pp.isJoinable = TRUE
                AND (:age IS NULL OR pp.minAge IS NULL OR pp.minAge <= :age)
                AND (:age IS NULL OR pp.maxAge IS NULL OR pp.maxAge >= :age)
                AND (:annualIncome IS NULL OR pp.earnMaxAmt IS NULL OR pp.earnMaxAmt >= :annualIncome)
                AND (:isHomeless IS NULL OR pp.requiresHomeless = FALSE OR :isHomeless = TRUE)
                AND (:isHouseholder IS NULL OR pp.requiresHouseholder = FALSE OR :isHouseholder = TRUE)
                AND (:tenureMonths IS NULL OR pp.minTenureMonths IS NULL OR pp.minTenureMonths <= :tenureMonths)
                AND (:monthlyDeposit IS NULL OR pp.maxMonthlyLimit IS NULL OR pp.maxMonthlyLimit >= :monthlyDeposit)
            """)
    List<Product> findEligibleProducts(
            @Param("age") Integer age,
            @Param("annualIncome") Long annualIncome,
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

    //

}
