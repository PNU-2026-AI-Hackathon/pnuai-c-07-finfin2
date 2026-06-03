package apptive.fin.search.repository;

import apptive.fin.search.entity.ProductProperty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductPropertyRepository extends JpaRepository<ProductProperty, Long> {
    List<ProductProperty> findByProductId(Long productId);
}
