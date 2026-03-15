package com.project.posts.service;

import com.project.posts.model.dto.LoginRequest;
import com.project.posts.model.dto.TokenResponse;
import com.project.posts.model.entity.User;
import com.project.posts.repository.UserRepository;
import com.project.posts.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository; // A "chave" do banco está aqui
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public Optional<TokenResponse> login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            var user = userRepository.findByUsername(request.username())
                    .orElseThrow();

            String token = jwtService.generateToken(user.getUsername());
            return Optional.of(new TokenResponse(token));

        } catch (Exception e) { // Mudamos de AuthenticationException para Exception temporariamente
            System.out.println("ERRO NO LOGIN: " + e.getMessage()); // Veja o log do IntelliJ!
            return Optional.empty();
        }
    }

    public String register(LoginRequest request) {
        // 1. Verificamos se o usuário já existe para evitar duplicidade
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Este nome de usuário já está em uso.");
        }

        // 2. Criamos a nova entidade de usuário
        User user = new User();
        user.setUsername(request.username());

        // 3. CRUCIAL: Criptografamos a senha antes de enviar para o banco
        String hash = passwordEncoder.encode(request.password());
        user.setPasswordHash(hash);

        // 4. Salvamos no banco
        userRepository.save(user);

        return "Usuário registrado com sucesso!";
    }
}
