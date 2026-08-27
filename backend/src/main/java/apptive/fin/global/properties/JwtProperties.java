package apptive.fin.global.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    String secret,
    int expiration,
    int refreshExpiration
) {
}
