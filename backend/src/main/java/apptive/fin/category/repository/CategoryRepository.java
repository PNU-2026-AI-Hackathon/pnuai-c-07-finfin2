package apptive.fin.category.repository;

import apptive.fin.category.dto.CategoryFlatDto;
import apptive.fin.category.entity.Category;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

    @Query("""
          SELECT new apptive.fin.category.dto.CategoryFlatDto(
            c.id, c.name, o.id, o.value
          )
          FROM Category c
          JOIN CategoryOption o ON c.id = o.category.id
          ORDER BY c.id, o.id
    """)
    List<CategoryFlatDto> findAllWithOptions();
}
