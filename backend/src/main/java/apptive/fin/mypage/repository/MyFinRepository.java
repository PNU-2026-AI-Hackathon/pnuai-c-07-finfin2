package apptive.fin.mypage.repository;

import apptive.fin.mypage.entity.MyFin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MyFinRepository extends JpaRepository<MyFin, Long> {

    @Query("SELECT m FROM MyFin m " +
            "JOIN FETCH m.productProperty pp " +
            "JOIN FETCH pp.product p " +
            "JOIN FETCH p.source " +
            "JOIN FETCH pp.provider " +
            "LEFT JOIN FETCH pp.keywords " +
            "WHERE m.user.id = :userId " +
            "ORDER BY m.createdAt DESC")
    List<MyFin> findAllByUserIdWithDetails(@Param("userId") Long userId);

    int countByUserId(Long userId);

    boolean existsByUserIdAndProductPropertyId(Long userId, Long productPropertyId);

    Optional<MyFin> findByUserIdAndProductPropertyId(Long userId, Long productPropertyId);

    void deleteByUserIdAndProductPropertyId(Long userId, Long productPropertyId);
}
