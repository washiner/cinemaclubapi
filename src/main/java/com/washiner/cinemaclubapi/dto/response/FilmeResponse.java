package com.washiner.cinemaclubapi.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FilmeResponse(

        Long id,
        String titulo,
        String descricao,
        String genero,
        Integer ano,
        BigDecimal nota,
        Boolean ativo,
        LocalDateTime criadoEm

) {
}
