package com.washiner.cinemaclubapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FilmeRequest(

        @NotBlank
        String titulo,

        String descricao,

        @NotBlank
        String genero,

        @NotNull
        Integer ano,

        @NotNull
        BigDecimal nota

) {
}
