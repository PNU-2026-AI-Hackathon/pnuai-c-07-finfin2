package apptive.fin.search.dto;

/**
 * 상품 신청 CTA 링크 해소 결과.
 * <ul>
 *     <li>{@code applyUrl} — 상품 자체 신청 페이지 URL(직접 이동). 없으면 null.</li>
 *     <li>{@code officialChannelUrl} — 직접 신청 URL이 없을 때 대신 안내할 기관 공식 채널 URL. 없으면 null.</li>
 *     <li>{@code officialChannelName} — 공식 채널 버튼 문구용 기관/채널명(예: "우리은행"). officialChannelUrl과 짝.</li>
 * </ul>
 * applyUrl과 officialChannel*은 상호배타: 상품 자체 URL이 있으면 채널은 내려가지 않는다.
 */
public record ApplyLink(String applyUrl, String officialChannelUrl, String officialChannelName) {
}
