package apptive.fin.user.entity;

import apptive.fin.global.entity.BaseTimeEntity;
import apptive.fin.user.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_provider_account", columnNames = {"provider", "provider_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "provider", length = 50, nullable = false)
    private String provider;

    @Column(name = "provider_id", length = 255, nullable = false)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 50)
    private UserRole userRole = UserRole.BEFORE_AGREED;



    @Builder
    public User(String name, String email, String provider, String providerId, UserRole userRole) {
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.userRole = userRole;
    }


    public String getOAuthIdentifier() {
        return provider + "_" + providerId;
    }

    public void updateUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public void updateName(String name) {
        this.name = name;
    }
    public void updateEmail(String email) {
        this.email = email;
    }
}
