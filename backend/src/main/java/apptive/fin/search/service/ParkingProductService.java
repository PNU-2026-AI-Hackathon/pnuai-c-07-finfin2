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
     * 최고금리(maxRate) 내림차순 정렬.
     */
    public List<ParkingProductDto> findParkingProducts(SearchRequestDto request) {
        // 파킹통장 상품 조회
        List<Product> parkingProducts = productRepository.findByType(ProductType.PARKING);

        // 가입 가능 상품 필터 (isJoinable)
        return parkingProducts.stream()
                .flatMap(product -> product.getProperties().stream()
                        .filter(ProductProperty::isJoinable)
                        .map(property -> toParkingProductDto(product, property)))
                .sorted(Comparator.comparingDouble(ParkingProductDto::maxRate).reversed())
                .toList();
    }

    /**
     * 예치액 기준 파킹통장 상품 필터링.
     * 예치액이 최고금리 적용 한도 내인 상품만 반환.
     */
    public List<ParkingProductDto> findParkingProductsWithDepositFilter(SearchRequestDto request) {
        Long depositAmount = request.depositAmount();

        return findParkingProducts(request).stream()
                .filter(dto -> {
                    // 적용 한도가 없거나, 예치액이 한도 이내인 경우 포함
                    if (dto.applicableLimit() == null || depositAmount == null) {
                        return true;
                    }
                    return depositAmount <= dto.applicableLimit();
                })
                .toList();
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

    private Double toDoubleOrNull(BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }

    private String determineInterestPaymentMethod(ProductProperty property) {
        // 파킹통장은 기본적으로 매일 이자 지급
        // TODO: 실제 이자 지급 방식 데이터가 있으면 그 값 사용
        return "매일";
    }
}
