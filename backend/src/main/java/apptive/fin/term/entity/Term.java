package apptive.fin.term.entity;

import apptive.fin.global.entity.BaseCreatedAtEntity;
import apptive.fin.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "terms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Term extends BaseCreatedAtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name="is_required", nullable = false)
    private boolean isRequired;


    @Builder
    public Term(String code, boolean isRequired) {
        this.code = code;
        this.isRequired = isRequired;
    }

}
