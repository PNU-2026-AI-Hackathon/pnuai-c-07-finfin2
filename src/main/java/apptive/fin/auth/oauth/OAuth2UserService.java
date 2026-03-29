package apptive.fin.auth.oauth;

import apptive.fin.auth.oauth.userinfo.OAuth2UserInfo;
import apptive.fin.auth.oauth.userinfo.OAuth2UserInfoFactory;
import apptive.fin.user.entity.User;
import apptive.fin.user.repository.UserRepository;
import apptive.fin.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public CustomOAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        String provider = userInfo.getProvider();
        String providerId = userInfo.getProviderId();
        String email = userInfo.getEmail();
        String name = userInfo.getName();

        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(()->userRepository.save(
                    User.builder()
                            .provider(provider)
                            .providerId(providerId)
                            .email(email)
                            .name(name)
                            .userRole(UserRole.BEFORE_AGREED)
                            .build()
                ));

        return new CustomOAuth2User(
                user, oAuth2User.getAttributes()
        );
    }

}
