package apptive.fin.apicollector.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "provider",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_provider_source_code",
                        columnNames = {"source_id", "code"}
                )
        }
)
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

    private String applyUrl;

    private Provider(ProductSource source, String code, String name) {
        this.source = source;
        this.code = code;
        this.name = name;
    }

    public static Provider create(ProductSource source, String code, String name) {
        return new Provider(source, code, name);
    }

    public void updateName(String name) {
        this.name = name;
    }
}
