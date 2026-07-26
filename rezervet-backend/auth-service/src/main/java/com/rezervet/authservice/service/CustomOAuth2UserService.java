package com.rezervet.authservice.service;

import com.rezervet.authservice.entity.User;
import com.rezervet.authservice.repository.AuthRepository;
import com.rezervet.authservice.utils.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AuthRepository authRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Google-dan məlumatı çək
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.debug("User {} loaded", oAuth2User);

        // 2. Məlumatları çıxart
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // 3. DB-də yoxla və ya yarat
        User user = authRepository.findUserByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .fullName(name)
                    .role(Role.USER)
                    .build();
            log.info("New User {} loaded", newUser);
            return authRepository.save(newUser);
        });

        // 4. Spring Security-yə qaytarmaq üçün istifadəçini xüsusi atributlarla təqdim et
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("userId", user.getId()); // DB-dəki ID-ni də əlavə edirik

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                attributes,
                "email"
        );
    }
}