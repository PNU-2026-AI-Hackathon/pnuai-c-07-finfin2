package apptive.fin.auth.security;


import apptive.fin.auth.AuthErrorCode;
import apptive.fin.global.error.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class BusinessAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException  {
        AuthErrorCode errorCode = AuthErrorCode.UNAUTHORIZED;

        response.setStatus(errorCode.getHttpStatus().value());
        response.setCharacterEncoding("UTF-8");

        String accept = request.getHeader("Accept");
        boolean wantsJson =
                accept == null
                || accept.contains("*/*")
                || accept.contains("application/json")
                || accept.contains("+json");

        if (wantsJson) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(
                    response.getWriter(),
                    ErrorResponseDto.of(errorCode)
            );
        }
        else {
            response.setContentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8");
            response.getWriter().write("");
        }
    }
}
