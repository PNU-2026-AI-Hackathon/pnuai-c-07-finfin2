package apptive.fin.apicollector.bankurl;

import apptive.fin.apicollector.product.ProductType;

public record BankProductUrlTarget(
        Long productId,
        String productCode,
        // 스크래퍼 검색어. 괄호를 뗀 디스플레이명(productName)이 아니라 원본 이름을 써야
        // 적립·지급 방식이 담긴 끝 괄호로 상품을 구분할 수 있다. 그래서 resolve 단계와 순서에 무관하다.
        String originalName,
        ProductType productType,
        String providerCode,
        String providerName
) {
}
