package apptive.fin.auth.security;

import apptive.fin.user.UserRole;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class AuthUserDetails extends User {
    private final Long id;

    public AuthUserDetails(Long id, UserRole role) {
        super(id.toString(), "",
                List.of(new SimpleGrantedAuthority(role.name()))
        );
        this.id = id;
    }
}
