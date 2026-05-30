package com.washiner.cinemaclubapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank
        String nome,

        @Email
        @NotBlank
        String email,

        @NotBlank
        String senha
){}
//Por que os dois no email?
//@Email valida o formato — mas não impede vazio!
//@NotBlank garante que não vem vazio.
//Sem @NotBlank → "" passa na validação do @Email!