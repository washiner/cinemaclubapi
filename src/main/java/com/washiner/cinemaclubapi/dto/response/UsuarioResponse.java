package com.washiner.cinemaclubapi.dto.response;

import com.washiner.cinemaclubapi.domain.enums.Role;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Role role,
        LocalDateTime criadoEm
) {
}
