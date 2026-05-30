package com.washiner.cinemaclubapi.controller;

import com.washiner.cinemaclubapi.domain.entity.Usuario;
import com.washiner.cinemaclubapi.dto.request.LoginRequest;
import com.washiner.cinemaclubapi.dto.request.RegisterRequest;
import com.washiner.cinemaclubapi.dto.response.AuthResponse;
import com.washiner.cinemaclubapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de registro, login, refresh e logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar novo usuário")
    public AuthResponse register(@RequestBody @Valid RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Fazer login e receber tokens")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar JWT usando refresh token")
    public AuthResponse refresh(@RequestBody String refreshToken) {
        return authService.refresh(refreshToken);
    }

    // @AuthenticationPrincipal → injeta o usuário autenticado automaticamente
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Logout e invalidar refresh token")
    public void logout(@AuthenticationPrincipal Usuario usuario) {
        authService.logout(usuario);
    }
}
