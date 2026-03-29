package apptive.fin.auth.entity;

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
@Table(name = "refresh_tokens",
    uniqueConstraints = {
        @UniqueConstraint(name="uq_refresh_tokens_token_hash", columnNames = "token_hash")
    },
    indexes = {
        @Index(name="idx_refresh_tokens_expires_at", columnList = "expires_at"),
        @Index(name="idx_refresh_tokens_user_id", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseCreatedAtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;


    @Builder
    public RefreshToken(String tokenHash, LocalDateTime expiresAt, User user) {
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.user = user;
        this.isActive = true;
    }

    public boolean checkValidity() {
        return isActive && expiresAt.isAfter(LocalDateTime.now());
    }
}
