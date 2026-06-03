package apptive.fin.category.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.util.List;

@Entity
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category")
    private List<CategoryOption> options;
}
