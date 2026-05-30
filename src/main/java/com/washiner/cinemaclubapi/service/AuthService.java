package com.washiner.cinemaclubapi.service;

import com.washiner.cinemaclubapi.config.JwtService;
import com.washiner.cinemaclubapi.domain.entity.RefreshToken;
import com.washiner.cinemaclubapi.domain.entity.Usuario;
import com.washiner.cinemaclubapi.domain.enums.Role;
import com.washiner.cinemaclubapi.dto.request.LoginRequest;
import com.washiner.cinemaclubapi.dto.request.RegisterRequest;
import com.washiner.cinemaclubapi.dto.response.AuthResponse;
import com.washiner.cinemaclubapi.exception.EmailJaCadastradoException;
import com.washiner.cinemaclubapi.exception.RefreshTokenInvalidoException;
import com.washiner.cinemaclubapi.repository.RefreshTokenRepository;
import com.washiner.cinemaclubapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    // ===== REGISTER =====
    // Cadastra novo usuário e devolve os tokens
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // Verifica se email já existe
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        // Cria o usuário com senha criptografada
        // Role padrão USER — admin só via banco
        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .role(Role.USER)
                .build();

        usuarioRepository.save(usuario);

        // Gera e devolve os tokens
        return gerarTokens(usuario);
    }

    // ===== LOGIN =====
    @Transactional
    public AuthResponse login(LoginRequest request) {

        // AuthenticationManager valida email e senha
        // Se errado → lança BadCredentialsException automaticamente
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.senha()
                )
        );

        // Credenciais corretas → busca usuário completo
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow();

        // Revoga tokens anteriores — evita acúmulo no banco
        refreshTokenRepository.revogarTodosPorUsuario(usuario);

        return gerarTokens(usuario);
    }

    // ===== REFRESH =====
    @Transactional
    public AuthResponse refresh(String refreshTokenString) {

        // Busca o token no banco
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(refreshTokenString)
                .orElseThrow(RefreshTokenInvalidoException::new);

        // Foi revogado?
        if (refreshToken.isRevogado()) {
            throw new RefreshTokenInvalidoException();
        }

        // Expirou?
        if (refreshToken.getExpiraEm().isBefore(LocalDateTime.now())) {
            throw new RefreshTokenInvalidoException();
        }

        // Válido → revoga o atual e gera novo par
        // Rotation — token antigo não pode ser reutilizado
        refreshToken.setRevogado(true);
        refreshTokenRepository.save(refreshToken);

        return gerarTokens(refreshToken.getUsuario());
    }

    // ===== LOGOUT =====
    @Transactional
    public void logout(Usuario usuario) {
        // Revoga todos os tokens do usuário
        refreshTokenRepository.revogarTodosPorUsuario(usuario);
    }

    // ===== MÉTODO PRIVADO =====
    // Gera JWT + RefreshToken — usado por register, login e refresh
    private AuthResponse gerarTokens(Usuario usuario) {

        // JWT — válido 15 minutos
        String accessToken = jwtService.gerarToken(usuario);

        // RefreshToken — UUID aleatório, válido 7 dias
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .usuario(usuario)
                .expiraEm(LocalDateTime.now()
                        .plusSeconds(refreshExpiration / 1000))
                .build();

        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, refreshToken.getToken());
    }
}