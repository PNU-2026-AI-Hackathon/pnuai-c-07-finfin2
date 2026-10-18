package apptive.fin.search.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "product_source")
public class ProductSource {

    // 소스 코드 상수(정부=온통청년, 은행=금융감독원 공시).
    public static final String GOVERNMENT_CODE = "ONTONG";
    public static final String BANK_CODE = "FSS";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;
}
