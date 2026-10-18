package apptive.fin.apicollector.product.repository;

import apptive.fin.apicollector.product.entity.ProductSource;
import apptive.fin.apicollector.product.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findBySourceAndCode(ProductSource source, String code);
}
