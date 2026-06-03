package apptive.fin.search.repository;

import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.entity.ProductKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductKeywordRepository extends JpaRepository<ProductKeyword, Long> {
    List<ProductKeyword> findByProductPropertyId(Long productPropertyId);
    List<ProductKeyword> findByKeywordCode(KeywordValueEnum keywordCode);
}
