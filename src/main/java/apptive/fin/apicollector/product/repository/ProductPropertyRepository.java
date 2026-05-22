package apptive.fin.apicollector.product.repository;

import apptive.fin.apicollector.product.entity.ProductProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductPropertyRepository extends JpaRepository<ProductProperty, Long> {

    @Query("""
            select pp.maxRate
            from ProductProperty pp
            where pp.isJoinable = true
              and pp.maxRate is not null
            order by pp.maxRate
            """)
    List<BigDecimal> findJoinableMaxRatesOrderByMaxRate();

    @Modifying
    @Query(value = """
            delete from product_property_keyword ppk
            where ppk.keyword_code = :keywordCode
            """, nativeQuery = true)
    int deleteHighInterestKeywords(@Param("keywordCode") String keywordCode);

    @Modifying
    @Query(value = """
            delete from product_property_keyword ppk
            where ppk.keyword_code = :keywordCode
              and not exists (
                  select 1
                  from product_properties pp
                  where pp.id = ppk.product_property_id
                    and pp.is_joinable = true
                    and pp.max_rate is not null
                    and pp.max_rate > :median
              )
            """, nativeQuery = true)
    int deleteHighInterestKeywordsNotExceedingMedian(
            @Param("keywordCode") String keywordCode,
            @Param("median") BigDecimal median
    );

    @Modifying
    @Query(value = """
            insert into product_property_keyword (product_property_id, keyword_code)
            select pp.id, :keywordCode
            from product_properties pp
            where pp.is_joinable = true
              and pp.max_rate is not null
              and pp.max_rate > :median
              and not exists (
                  select 1
                  from product_property_keyword ppk
                  where ppk.product_property_id = pp.id
                    and ppk.keyword_code = :keywordCode
              )
            """, nativeQuery = true)
    int insertMissingHighInterestKeywords(
            @Param("keywordCode") String keywordCode,
            @Param("median") BigDecimal median
    );
}
