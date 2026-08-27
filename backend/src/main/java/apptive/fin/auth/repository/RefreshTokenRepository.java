package apptive.fin.auth.repository;

import apptive.fin.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteByTokenHash(String tokenHash);

    @Modifying
    @Query("update RefreshToken r set r.isActive = false where r.tokenHash = :tokenHash and r.isActive = true")
    int deactivateIfActive(String tokenHash);
}
