package apptive.fin.myfin.service;

import apptive.fin.global.error.BusinessException;
import apptive.fin.myfin.MyFinErrorCode;
import apptive.fin.myfin.dto.MyfinResponseDto;
import apptive.fin.myfin.entity.MyFin;
import apptive.fin.myfin.repository.MyFinRepository;
import apptive.fin.search.dto.BankDetailDto;
import apptive.fin.search.dto.GovernmentDetailDto;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductKeyword;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.repository.ProductPropertyRepository;
import apptive.fin.search.service.MatchScoreService;
import apptive.fin.search.service.RateCalculatorService;
import apptive.fin.user.entity.User;
import apptive.fin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyFinService {

    private static final int MAX_FIN = 20;

    private final MyFinRepository myfinRepository;
    private final UserRepository userRepository;
    private final ProductPropertyRepository productPropertyRepository;
    private final MatchScoreService matchScoreService;
    private final RateCalculatorService rateCalculatorService;

    // 찜 목록 조회
    // 최신 배치 데이터 + 프로필 기준 재계산
    // 찜 등록 최신순 정렬
    public MyfinResponseDto.List_ getFavorites(Long userId, SearchRequestDto request) {
        List<MyFin> favorites = myfinRepository.findAllByUserIdWithDetails(userId);

        if (favorites.isEmpty()) {
            return new MyfinResponseDto.List_(List.of(), false);
        }

        ResolvedKeywords keywords = ResolvedKeywords.emptyKeywords();

        List<MyfinResponseDto.Item> items = favorites.stream()
                .map(myFin -> toItemDto(myFin, request, keywords))
                .toList();

        // 정부(ONTONG)와 은행(FSS) 상품이 혼재되어 있는지 확인
        boolean hasOntong = items.stream().anyMatch(i -> "ONTONG".equals(i.sourceCode()));
        boolean hasFss = items.stream().anyMatch(i -> "FSS".equals(i.sourceCode()));
        boolean showComparisonNotice = hasOntong && hasFss;

        return new MyfinResponseDto.List_(items, showComparisonNotice);
    }

    // 프로필 없이 조회 (기본값 사용)
    public MyfinResponseDto.List_ getFavorites(Long userId) {
        return getFavorites(userId, null);
    }

    // 찜 추가
    @Transactional
    public void addFavorite(Long userId, Long productPropertyId) {
        int currentCount = myfinRepository.countByUserId(userId);
        if (currentCount >= MAX_FIN) {
            throw new BusinessException(MyFinErrorCode.FAVORITE_LIMIT_EXCEEDED);
        }

        if (myfinRepository.existsByUserIdAndProductPropertyId(userId, productPropertyId)) {
            throw new BusinessException(MyFinErrorCode.FAVORITE_ALREADY_EXISTS);
        }

        ProductProperty productProperty = productPropertyRepository.findById(productPropertyId)
                .orElseThrow(() -> new BusinessException(MyFinErrorCode.PRODUCT_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(MyFinErrorCode.PRODUCT_NOT_FOUND));

        MyFin myFin = MyFin.builder()
                .user(user)
                .productProperty(productProperty)
                .build();

        myfinRepository.save(myFin);
    }

    // 찜 삭제
    @Transactional
    public void removeFavorite(Long userId, Long productPropertyId) {
        if (!myfinRepository.existsByUserIdAndProductPropertyId(userId, productPropertyId)) {
            throw new BusinessException(MyFinErrorCode.FAVORITE_NOT_FOUND);
        }

        myfinRepository.deleteByUserIdAndProductPropertyId(userId, productPropertyId);
    }

    // 찜 여부 확인
    public boolean isFavorite(Long userId, Long productPropertyId) {
        return myfinRepository.existsByUserIdAndProductPropertyId(userId, productPropertyId);
    }

    // 현재 찜 개수 조회
    public int getFavoriteCount(Long userId) {
        return myfinRepository.countByUserId(userId);
    }

    private MyfinResponseDto.Item toItemDto(MyFin myFin, SearchRequestDto request, ResolvedKeywords keywords) {
        ProductProperty pp = myFin.getProductProperty();
        Product product = pp.getProduct();
        String sourceCode = product.getSource().getCode();

        // 키워드 추출
        List<String> keywordList = pp.getKeywords().stream()
                .map(ProductKeyword::getKeywordCode)
                .map(Enum::name)
                .toList();

        // 적합도 계산 (MatchScoreService 활용)
        Integer fitScore = calculateFitScore(product, pp, request, keywords);

        // 요약 라인 생성
        String summaryLine = buildSummaryLine(pp, sourceCode);

        // 지표 계산 (RateCalculatorService 활용)
        MyfinResponseDto.Metrics metrics = calculateMetrics(pp, sourceCode, request, keywords);

        // 계산 기준 캡션
        String calcBasisCaption = buildCalcBasisCaption(pp, sourceCode, request);

        // 신청 상태 확인
        String applyStatus = determineApplyStatus(pp, sourceCode);

        // 신청 URL
        String applyUrl = pp.resolvedApplyUrl();

        return new MyfinResponseDto.Item(
                myFin.getId(),
                pp.getId(),
                sourceCode,
                product.getProductCode(),
                product.getProductName(),
                pp.getSaveTrm() != null ? String.valueOf(pp.getSaveTrm()) : null,
                fitScore,
                keywordList,
                summaryLine,
                metrics,
                calcBasisCaption,
                pp.getExcludeFromRateComparison(),
                applyStatus,
                applyUrl
        );
    }

    private Integer calculateFitScore(Product product, ProductProperty pp, SearchRequestDto request, ResolvedKeywords keywords) {
        if (request == null) {
            return null;
        }

        ProductMatchDto matchDto = matchScoreService.score(product, pp, request, keywords, false);
        // totalScore를 0~100 스케일로 변환
        return (int) Math.round(matchDto.totalScore() * 100);
    }

    private MyfinResponseDto.Metrics calculateMetrics(ProductProperty pp, String sourceCode, SearchRequestDto request, ResolvedKeywords keywords) {
        if ("ONTONG".equals(sourceCode)) {
            // 정부 상품: RateCalculatorService.governmentDetail() 활용
            GovernmentDetailDto govDetail = rateCalculatorService.governmentDetail(pp, request);

            Double contributionYieldRate = govDetail != null ? govDetail.annualizedYield() : null;
            Long expectedMaturityAmount = govDetail != null ? govDetail.expectedTotalContribution() : null;

            return new MyfinResponseDto.Metrics(
                    contributionYieldRate,
                    expectedMaturityAmount,
                    null, null, null
            );
        } else {
            // 은행 상품: RateCalculatorService.bankDetail() 활용
            BankDetailDto bankDetail = rateCalculatorService.bankDetail(pp, request, keywords);

            Double baseRate = bankDetail != null ? bankDetail.baseRate() :
                    (pp.getBaseRate() != null ? pp.getBaseRate().doubleValue() : null);
            Double maxRate = bankDetail != null ? bankDetail.maxRate() :
                    (pp.getMaxRate() != null ? pp.getMaxRate().doubleValue() : null);
            Double achievableRate = bankDetail != null ? bankDetail.achievableRate() : baseRate;

            return new MyfinResponseDto.Metrics(
                    null, null,
                    baseRate, maxRate, achievableRate
            );
        }
    }

    private String buildSummaryLine(ProductProperty pp, String sourceCode) {
        StringBuilder sb = new StringBuilder();

        if ("ONTONG".equals(sourceCode)) {
            sb.append("정부 지원");
        } else {
            sb.append(pp.getProvider().getName());
        }

        // 만기
        if (pp.getSaveTrm() != null) {
            int months = pp.getSaveTrm();
            if (months >= 12 && months % 12 == 0) {
                sb.append(" · ").append(months / 12).append("년 만기");
            } else {
                sb.append(" · ").append(months).append("개월 만기");
            }
        }

        // 월 납입 범위
        if (pp.getMinMonthlyLimit() != null || pp.getMaxMonthlyLimit() != null) {
            sb.append(" · 월 ");
            if (pp.getMinMonthlyLimit() != null) {
                sb.append(formatAmount(pp.getMinMonthlyLimit()));
            }
            if (pp.getMaxMonthlyLimit() != null) {
                sb.append("~").append(formatAmount(pp.getMaxMonthlyLimit()));
            }
        }

        return sb.toString();
    }

    private String formatAmount(Long amount) {
        if (amount >= 10000) {
            return (amount / 10000) + "만원";
        } else if (amount >= 1000) {
            return (amount / 1000) + "천원";
        }
        return amount + "원";
    }

    private String buildCalcBasisCaption(ProductProperty pp, String sourceCode, SearchRequestDto request) {
        if ("ONTONG".equals(sourceCode)) {
            if (request != null && request.detailedOptions() != null) {
                Long monthly = request.detailedOptions().monthlySavingsGoal();
                if (monthly != null) {
                    return "월 " + formatAmount(monthly) + " · " +
                           (pp.getSaveTrm() != null ? pp.getSaveTrm() + "개월" : "만기") + " 기준";
                }
            }
            return "매칭 방식 · 월 납입액 · 만기 기준 산출";
        } else {
            return "내 조건 기준 달성 가능 금리";
        }
    }

    private String determineApplyStatus(ProductProperty pp, String sourceCode) {
        if ("ONTONG".equals(sourceCode)) {
            if (!pp.getIsJoinable()) {
                return "RECRUIT_CLOSED";
            }
        }
        return "AVAILABLE";
    }
}
