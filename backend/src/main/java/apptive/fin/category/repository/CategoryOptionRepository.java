package apptive.fin.category.repository;

import apptive.fin.category.entity.CategoryOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryOptionRepository extends JpaRepository<CategoryOption, Long> {
}
