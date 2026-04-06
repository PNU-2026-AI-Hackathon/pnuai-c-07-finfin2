package apptive.fin.category.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class CategoryOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name  = "category_id")
    private Category category ;
}
