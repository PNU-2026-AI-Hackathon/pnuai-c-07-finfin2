package apptive.fin.search.util;

import apptive.fin.provider.entity.Provider;
import apptive.fin.search.dto.ApplyLink;
import apptive.fin.search.entity.ProductProperty;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class ApplyLinkResolverTest {

    @Test
    void 상품_자체_신청URL이_있으면_applyUrl로_내려가고_공식채널은_비운다() {
        ProductProperty property = property(true, "https://product.example/apply", "https://provider.example/apply", "국민은행");

        ApplyLink link = ApplyLinkResolver.resolve(property);

        assertThat(link.applyUrl()).isEqualTo("https://product.example/apply");
        assertThat(link.officialChannelUrl()).isNull();
        assertThat(link.officialChannelName()).isNull();
    }

    @Test
    void 상품_직접URL이_없으면_기관_공식채널_URL과_이름을_내려준다() {
        ProductProperty property = property(true, null, "https://provider.example/apply", "국민은행");

        ApplyLink link = ApplyLinkResolver.resolve(property);

        assertThat(link.applyUrl()).isNull();
        assertThat(link.officialChannelUrl()).isEqualTo("https://provider.example/apply");
        assertThat(link.officialChannelName()).isEqualTo("국민은행");

        // 빈 문자열도 '없음'으로 취급 — 공식 채널로 폴백한다.
        ReflectionTestUtils.setField(property, "applyUrl", "   ");
        ApplyLink blank = ApplyLinkResolver.resolve(property);
        assertThat(blank.applyUrl()).isNull();
        assertThat(blank.officialChannelUrl()).isEqualTo("https://provider.example/apply");
    }

    @Test
    void 상품도_기관도_URL이_없으면_전부_null() {
        ProductProperty blankProvider = property(true, null, "   ", "국민은행");
        ApplyLink link = ApplyLinkResolver.resolve(blankProvider);
        assertThat(link.applyUrl()).isNull();
        assertThat(link.officialChannelUrl()).isNull();
        assertThat(link.officialChannelName()).isNull();

        // provider 자체가 없어도 안전하게 전부 null.
        ProductProperty noProvider = property(true, null, null, null);
        ReflectionTestUtils.setField(noProvider, "provider", null);
        ApplyLink noProviderLink = ApplyLinkResolver.resolve(noProvider);
        assertThat(noProviderLink.applyUrl()).isNull();
        assertThat(noProviderLink.officialChannelUrl()).isNull();
        assertThat(noProviderLink.officialChannelName()).isNull();
    }

    @Test
    void 마감_상품은_url이_있어도_전부_null() {
        ProductProperty closed = property(false, "https://product.example/apply", "https://provider.example/apply", "국민은행");

        ApplyLink link = ApplyLinkResolver.resolve(closed);

        assertThat(link.applyUrl()).isNull();
        assertThat(link.officialChannelUrl()).isNull();
        assertThat(link.officialChannelName()).isNull();
    }

    private ProductProperty property(boolean joinable, String propertyApplyUrl, String providerApplyUrl, String providerName) {
        Provider provider = new Provider();
        ReflectionTestUtils.setField(provider, "applyUrl", providerApplyUrl);
        ReflectionTestUtils.setField(provider, "name", providerName);

        ProductProperty property = new ProductProperty();
        ReflectionTestUtils.setField(property, "isJoinable", joinable);
        ReflectionTestUtils.setField(property, "applyUrl", propertyApplyUrl);
        ReflectionTestUtils.setField(property, "provider", provider);
        return property;
    }
}
