package apptive.fin.search.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name="provider")
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id",nullable = false)
    private ProductSource source;

    private String code;

    @Column(nullable = false)
    private String name;
}
