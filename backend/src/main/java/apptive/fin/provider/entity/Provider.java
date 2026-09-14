package apptive.fin.provider.entity;

import apptive.fin.search.entity.ProductSource;
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

    // 은행(FSS) 신청 페이지 대표 URL. 수동 큐레이션 값이며 동기화(name만 갱신)에 영향받지 않는다.
    // NULL = 링크 없음. broken 링크는 NULL로 되돌린다.
    private String applyUrl;
}
