package apptive.fin.auth.oauth;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

@Getter
public class CustomOAuth2User extends User implements OAuth2User {

    private final apptive.fin.user.entity.User user;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(apptive.fin.user.entity.User user, Map<String, Object> attributes) {
        super(user.getOAuthIdentifier(), "", List.of(new SimpleGrantedAuthority(user.getUserRole().toString())));
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return user.getName();
    }


}
