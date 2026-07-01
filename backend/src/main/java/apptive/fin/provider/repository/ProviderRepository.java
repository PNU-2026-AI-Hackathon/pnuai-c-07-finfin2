package apptive.fin.provider.repository;

import apptive.fin.provider.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
    Optional<Provider> findByCode(String code);

    List<Provider> findBySource_CodeOrderByNameAsc(String sourceCode);
}
