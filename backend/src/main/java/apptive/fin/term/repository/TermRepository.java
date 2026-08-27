package apptive.fin.term.repository;

import apptive.fin.term.dto.TermResponseDto;
import apptive.fin.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TermRepository extends JpaRepository<Term,Long> {
    @Query("""
        SELECT NEW apptive.fin.term.dto.TermResponseDto(
            t.id,
            tv.id,
            t.code,
            tv.title,
            tv.content,
            tv.effectiveFrom,
            t.isRequired,
            CASE WHEN EXISTS (
                SELECT 1 FROM UserTermAgreement uta
                JOIN uta.termVersion agreedTv
                WHERE uta.user.id = :userId
                  AND agreedTv.term = t
                  AND agreedTv.majorVersion = tv.majorVersion
                  AND uta.agreed = true
            ) THEN true ELSE false END
        )
        FROM Term t
        JOIN TermVersion tv ON tv.term = t
        WHERE tv.isCurrent = true
    """)
    List<TermResponseDto> getTermResponseDtosByUserId(Long userId);
}
