package com.project.posts.controller;

import com.project.posts.model.dto.LoginRequest;
import com.project.posts.model.dto.TokenResponse;
import com.project.posts.security.JwtService;
import com.project.posts.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 1. LOGIN MANUAL
    @CrossOrigin(origins = "*", allowedHeaders = "*") // Para desenvolvimento, permite tudo
    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest request) {
        return authService.login(request)
                // Usamos o <ResponseEntity<?>> antes do map para "destravar" o tipo
                .<ResponseEntity<?>>map(token -> ResponseEntity.ok(token))
                .orElse(ResponseEntity.ok(new java.util.ArrayList<>()));
    }

    // 2. CADASTRO
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. PONTO DE ENTRADA DO GOOGLE
    @GetMapping("/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }
}