package apptive.fin.term.entity;

import apptive.fin.global.entity.BaseCreatedAtEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "term_versions", uniqueConstraints = {
        @UniqueConstraint(name = "uq_term_versions_version", columnNames = {"term_id", "major_version", "minor_version"})
},
indexes = {
        @Index(name = "uq_term_version_current_per_term", columnList = "term_id")
}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermVersion extends BaseCreatedAtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Term term;

    @Column(name = "major_version", nullable = false)
    private Integer majorVersion;

    @Column(name = "minor_version", nullable = false)
    private Integer minorVersion;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_current", nullable = false)
    private boolean isCurrent;

    @Column(name = "effective_from", nullable = false)
    private LocalDateTime effectiveFrom;
}
