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
            LEFT JOIN FETCH p.options
            WHERE p.isJoinable = TRUE
                AND (p.minAge IS NULL OR p.minAge <= :age)
                AND (p.maxAge IS NULL OR p.maxAge >= :age)
                AND (p.earnMaxAmt IS NULL OR p.earnMaxAmt >= :annualIncome)
                AND (p.requiresHomeless = FALSE OR :isHomeless = TRUE)
                AND (p.requiresHouseholder = FALSE OR :isHouseholder = TRUE)
                AND (p.minTenureMonths IS NULL OR p.minTenureMonths <= :tenureMonths OR :tenureMonths IS NULL)
                AND (p.maxMonthlyLimit IS NULL OR p.maxMonthlyLimit >= :monthlyDeposit)
            """)
    List<Product> findEligibleProducts(
            @Param("age") int age,
            @Param("annualIncome") long annualIncome,
            @Param("isHomeless")     boolean isHomeless,
            @Param("isHouseholder")  boolean isHouseholder,
            @Param("tenureMonths")   int tenureMonths,
            @Param("monthlyDeposit") long monthlyDeposit
    );

    // 키워드 기준 조회
    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN FETCH p.options
        LEFT JOIN FETCH p.keywords
        JOIN p.keywords k
        WHERE k.keywordCode IN :keywords
    """)
    List<Product> findByKeywords(@Param("keywords") List<KeywordValueEnum> keywords);

}
