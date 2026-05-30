package com.washiner.cinemaclubapi.dto.response;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType
) {
    // Construtor auxiliar — só 2 campos
    // tokenType é sempre "Bearer" — não precisa passar
    public AuthResponse (String accessToken, String refreshToken){
        this(accessToken, refreshToken, "Bearer");
    }
}


//De onde vieram os 3 campos?
//Não precisa decorar — vem do fluxo:
//Usuário faz login
//      ↓
//API precisa devolver o quê?
//
//1. O JWT para usar nas requisições → accessToken
//2. O cupom de renovação           → refreshToken
//3. Como usar o token no header    → tokenType = "Bearer"
//
//🔑 Raciocínio: pensa no que o frontend precisa receber
//para conseguir usar a API — os 3 campos aparecem naturalmente.