package apptive.fin.search.util;

import apptive.fin.provider.entity.Provider;
import apptive.fin.search.dto.ApplyLink;
import apptive.fin.search.entity.ProductProperty;

/**
 * 상품 신청 CTA 링크({@link ApplyLink})를 해소한다. 상세·찜 등 CTA가 있는 응답이 공용으로 재사용한다.
 * 우선순위: 마감이면 전부 null → 상품 자체 신청 URL 있으면 그걸(채널 비움) → 아니면 기관 공식 채널 URL/이름 → 없으면 전부 null.
 * 가입 가능 여부 판정은 {@link ProductAvailability#isJoinable}에 위임한다.
 */
public final class ApplyLinkResolver {

    private ApplyLinkResolver() {
    }

    public static ApplyLink resolve(ProductProperty property) {
        if (!ProductAvailability.isJoinable(property)) {
            return new ApplyLink(null, null, null);
        }
        String applyUrl = property.getApplyUrl();
        if (applyUrl != null && !applyUrl.isBlank()) {
            return new ApplyLink(applyUrl, null, null);
        }
        Provider provider = property.getProvider();
        if (provider == null) {
            return new ApplyLink(null, null, null);
        }
        String channelUrl = provider.getApplyUrl();
        if (channelUrl == null || channelUrl.isBlank()) {
            return new ApplyLink(null, null, null);
        }
        return new ApplyLink(null, channelUrl, provider.getName());
    }
}
