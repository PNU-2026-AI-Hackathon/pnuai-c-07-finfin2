package apptive.fin.search.service;

import apptive.fin.search.dto.ParkingProductDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.enums.ProductType;
import apptive.fin.search.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 파킹통장 서비스.
 * 단기예치 대분류의 파킹통장 탭에 표시할 상품 목록을 제공한다.
 * - 최고금리순 정렬
 * - 비로그인 허용
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParkingProductService {

    private final ProductRepository productRepository;

    /**
     * 파킹통장 상품 목록 조회.
     * - 자격 필터: 미성년(17세 미만) 제외, 예치금 하한 체크
     * - 최고금리(maxRate) 내림차순 정렬
     */
    public List<ParkingProductDto> findParkingProducts(SearchRequestDto request) {
        List<Product> parkingProducts = productRepository.findByType(ProductType.PARKING);

        Integer age = request.age();
        Long depositAmount = request.depositAmount();

        // PRD 동점 규칙: 최고금리 → 기본금리 → 상품명
        return parkingProducts.stream()
                .flatMap(product -> product.getProperties().stream()
                        .filter(ProductProperty::isJoinable)
                        .filter(property -> isAgeEligible(property, age))
                        .filter(property -> isDepositEligible(property, depositAmount))
                        .map(property -> toParkingProductDto(product, property)))
                .sorted(parkingComparator())
                .toList();
    }

    /**
     * 나이 자격 확인.
     * - 단기예치: 미성년(만 17세 미만) 일괄 제외
     * - 상품별 나이 제한 체크
     */
    private boolean isAgeEligible(ProductProperty property, Integer age) {
        // 미성년(17세 미만) 일괄 제외
        if (age != null && age < 17) {
            return false;
        }
        // 상품 최소 나이 제한
        if (age != null && property.getMinAge() != null && property.getMinAge() > age) {
            return false;
        }
        // 상품 최대 나이 제한
        return property.getMaxAge() == null || age == null || property.getMaxAge() >= age;
    }

    /**
     * 예치금 하한 확인.
     * 상품의 최소 한도보다 예치액이 적으면 제외.
     */
    private boolean isDepositEligible(ProductProperty property, Long depositAmount) {
        return depositAmount == null
                || property.getMinMonthlyLimit() == null
                || property.getMinMonthlyLimit() <= depositAmount;
    }

    private ParkingProductDto toParkingProductDto(Product product, ProductProperty property) {
        return ParkingProductDto.builder()
                .productId(product.getId())
                .productPropertyId(property.getId())
                .productName(product.getProductName())
                .providerName(property.providerName())
                .providerCode(property.getProvider() != null ? property.getProvider().getCode() : null)
                .baseRate(toDoubleOrNull(property.getBaseRate()))
                .maxRate(toDoubleOrNull(property.getMaxRate()))
                .applicableLimit(property.getMaxMonthlyLimit())  // 최고금리 적용 한도로 사용
                .interestPaymentMethod(determineInterestPaymentMethod(property))
                .depositProtection(true)  // 은행 상품은 기본 예금자보호
                .applyUrl(property.getApplyUrl())
                .officialChannelUrl(null)  // TODO: 공식 채널 URL 필드 추가 시 연동
                .officialChannelName(property.providerName())
                .build();
    }

    /**
     * 파킹통장 정렬 Comparator.
     * PRD 동점 규칙: 최고금리 → 기본금리 → 상품명
     */
    private Comparator<ParkingProductDto> parkingComparator() {
        // 내림차순을 위해 음수로 변환
        return Comparator
                .comparingDouble((ParkingProductDto dto) -> -(dto.maxRate() != null ? dto.maxRate() : 0.0))
                .thenComparingDouble((ParkingProductDto dto) -> -(dto.baseRate() != null ? dto.baseRate() : 0.0))
                .thenComparing(ParkingProductDto::productName, Comparator.nullsLast(Comparator.naturalOrder()));
    }

    private Double toDoubleOrNull(BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }

    private String determineInterestPaymentMethod(ProductProperty property) {
        // 파킹통장은 기본적으로 매일 이자 지급
        // TODO: 실제 이자 지급 방식 데이터가 있으면 그 값 사용
        return "매일";
    }
}
