package com.rezervet.authservice.security;

import com.rezervet.authservice.entity.User;
import com.rezervet.authservice.repository.AuthRepository;
import com.rezervet.authservice.service.JwtService;
import com.rezervet.authservice.utils.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final AuthRepository authRepository;

    @Value("${spring.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        // DİQQƏT: Google scope-unda "openid" olduğu üçün Spring Security bunu OIDC axını sayır
        // və CustomOAuth2UserService (adi OAuth2 userService) heç vaxt çağırılmır — ona görə
        // find-or-create məntiqini birbaşa burada, principal-ın email claim-i üzərindən edirik.
        // Bu, həm OAuth2, həm OIDC axınında eyni cür işləyir (hər ikisi email/name claim-i verir).
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        log.debug("OAuth2 login: email={}", email);

        User user = authRepository.findUserByEmail(email)
                .orElseGet(() -> authRepository.save(
                        User.builder()
                                .email(email)
                                .fullName(name)
                                .role(Role.USER)
                                .build()
                ));

        // Mövcud User obyekti ilə (DB-dən yenidən axtarmadan) token yaradırıq
        String token = jwtService.generateToken(user);

        String redirectUrl = frontendUrl + "/oauth-success?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}
