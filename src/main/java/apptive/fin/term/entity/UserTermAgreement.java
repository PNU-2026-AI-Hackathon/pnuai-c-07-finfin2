package apptive.fin.term.entity;

import apptive.fin.global.entity.BaseCreatedAtEntity;
import apptive.fin.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_term_agreements", uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_term_agreements_user_version", columnNames = {"user_id", "term_version_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTermAgreement extends BaseCreatedAtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_version_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TermVersion termVersion;

    @Column(name = "agreed", nullable = false)
    private boolean agreed;

    @Column(name = "agreed_at")
    private LocalDateTime agreedAt = null;


    @Builder
    public UserTermAgreement(User user, TermVersion termVersion, boolean agreed, LocalDateTime agreedAt) {
        this.user = user;
        this.termVersion = termVersion;
        this.agreed = agreed;
        this.agreedAt = agreedAt;
    }

    public void agree(){
        this.agreed = true;
        this.agreedAt = LocalDateTime.now();
    }

    public void disagree() {
        this.agreed = false;
        this.agreedAt = null;
    }
}
