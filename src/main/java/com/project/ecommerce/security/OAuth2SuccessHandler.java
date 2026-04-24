package com.project.ecommerce.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ecommerce.dto.LoginResponseDto;
import com.project.ecommerce.exception.GenericException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    private final ObjectMapper objectMapper;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException,
            ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = token.getAuthorizedClientRegistrationId();
        ResponseEntity<LoginResponseDto> loginResponse =
                authService.handleOAuth2LoginRequest(oAuth2User, registrationId);
//        response.setStatus(loginResponse.getStatusCode().value());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.getWriter().write(objectMapper.writeValueAsString(loginResponse.getBody()));
//        request.getSession().setAttribute("JWT", loginResponse.getBody().getJwt());

        // Step 2: generate one-time auth code and store loginResponse in Redis
        String authCode = authService.storeLoginResponse(loginResponse.getBody());

        // Step 3: redirect frontend with auth code as query param
        String redirectUrl = frontendUrl + "/auth/login?authcode=" + authCode;
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.sendRedirect(redirectUrl);

    }
}
