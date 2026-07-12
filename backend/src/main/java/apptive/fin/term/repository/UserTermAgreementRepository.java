package apptive.fin.term.repository;

import apptive.fin.term.dto.TermResponseDto;
import apptive.fin.term.entity.Term;
import apptive.fin.term.entity.TermVersion;
import apptive.fin.term.entity.UserTermAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import apptive.fin.user.entity.User;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface UserTermAgreementRepository extends  JpaRepository<UserTermAgreement,Long> {
//    @Query("SELECT ut FROM UserTermAgreement ut JOIN FETCH ut.term WHERE ut.user = :user")
    List<UserTermAgreement> findAllByUser(User user);



    List<UserTermAgreement> findAllByUserAndTermVersionIn(User user, List<TermVersion> termVersion);

    Optional<UserTermAgreement> findByUserAndTermVersion(User user, TermVersion termVersion);

    @Query("""
        SELECT CASE WHEN COUNT(uta) > 0 THEN true ELSE false END
        FROM UserTermAgreement uta
        JOIN uta.termVersion tv
        JOIN tv.term t
        WHERE uta.user.id = :userId
          AND t.code = :code
          AND tv.isCurrent = true
          AND uta.agreed = true
    """)
    boolean existsCurrentAgreement(Long userId, String code);
}
