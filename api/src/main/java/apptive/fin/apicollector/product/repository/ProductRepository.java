package apptive.fin.apicollector.product.repository;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.product.entity.Product;
import apptive.fin.apicollector.product.entity.ProductSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySourceAndProductCode(ProductSource source, String productCode);

    // 디스플레이 이름 중복 판정은 사용자에게 실제로 보이는(가입 가능한 property가 하나 이상인) 상품끼리만 한다.
    @Query("select distinct p from Product p join p.properties pp where pp.isJoinable = true")
    List<Product> findAllWithJoinableProperty();

    @Query("""
        update ProductProperty pp
            set pp.isJoinable = false
           where pp.product.source = :productSource
           and exists(
               select pr.id
                   from ProductRaw pr
                   where pr.source = :source
                       and pr.externalId = pp.product.productCode
                       and pr.lastSeenAt < :lastSeen
               )
    """)
    @Modifying
    int disableBySourceAndLastSeenBefore(ProductSource productSource, Source source, Instant lastSeen);
}
