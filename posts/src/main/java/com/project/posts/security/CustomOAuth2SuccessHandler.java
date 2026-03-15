package com.project.posts.security;

import com.project.posts.model.entity.User;
import com.project.posts.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // 1. Lógica "Find or Create" (Mantendo consistência com o login nativo)
        User user = userRepository.findByUsername(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(email);
                    newUser.setCreatedAt(LocalDateTime.now());
                    // Geramos uma senha aleatória para usuários Google por segurança de banco
                    newUser.setPasswordHash(UUID.randomUUID().toString());
                    return userRepository.save(newUser);
                });

        // 2. Gerar o mesmo JWT que o login nativo usa
        String token = jwtService.generateToken(user.getUsername());

        // 3. REDIRECIONAMENTO (O segredo para o Flutter)
        // Usamos o esquema customizado que configuraremos no AndroidManifest.xml
        String targetUrl = UriComponentsBuilder.fromUriString("com.project.posts://auth")
                .queryParam("token", token)
                .build().toUriString();

        response.sendRedirect(targetUrl);
    }
}