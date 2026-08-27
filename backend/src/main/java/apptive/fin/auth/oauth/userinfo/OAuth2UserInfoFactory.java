package apptive.fin.auth.oauth.userinfo;

import apptive.fin.global.error.BusinessException;
import apptive.fin.global.error.CommonErrorCode;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            case "kakao" -> new KakaoOAuth2UserInfo(attributes);
            default -> throw new BusinessException(CommonErrorCode.NOT_FOUND);
        };
    }
}