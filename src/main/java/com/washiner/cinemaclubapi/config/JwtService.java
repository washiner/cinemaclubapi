package com.washiner.cinemaclubapi.config;

import com.washiner.cinemaclubapi.domain.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// @Service → bean do Spring
// Responsabilidade única: tudo relacionado a JWT fica aqui
@Service
public class JwtService {

    // @Value → lê do application.properties
    // jwt.secret=minha-chave-secreta...
    @Value("${jwt.secret}")
    private String secret;


    // Tempo de expiração em milissegundos — 15 minutos
    @Value("${jwt.expiration}")
    private Long expiration;


    //🔑 Raciocínio:
    //secret = "minha-chave-secreta..."  ← String simples
    //               ↓
    //getSigningKey() converte para SecretKey
    //               ↓
    //JWT usa SecretKey para assinar — não aceita String pura


    // Converte a String secret em chave criptográfica
    // HS256 exige chave de no mínimo 256 bits
    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Gera um JWT para o usuário
    // Coloca os Claims: sub, role, iat, exp
    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                // sub → quem é o dono do token (email)
                .subject(usuario.getEmail())
                // claim customizado → a role do usuário
                .claim("role", usuario.getRole().name())
                // quando foi gerado
                .issuedAt(new Date())
                // quando expira — agora + 15 minutos
                .expiration(new Date(System.currentTimeMillis() + expiration))
                // assina com nossa chave secreta
                .signWith(getSigningKey())
                .compact();


        //🔑 Raciocínio — o que é cada Claims:
        //subject  → quem é você → email
        //role     → o que pode fazer → ADMIN ou USER
        //issuedAt → quando foi gerado → agora
        //expiration → quando expira → agora + 15 min
        //signWith → assina o token → garante que não foi adulterado
    }

    // Extrai todos os Claims do token
    // Claims = todos os campos dentro do JWT
    private Claims extrairClaims(String token) {
        return Jwts.parser()
                // informa a chave para validar a assinatura
                .verifyWith(getSigningKey())
                .build()
                // lê e valida o token
                // lança exception se inválido ou expirado
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extrai só o email (subject) do token
    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    //🔑 Raciocínio:
    //extrairClaims → abre o token e lê tudo
    //extrairEmail  → usa extrairClaims e pega só o subject
    //
    //Privado → só usado internamente
    //Público → chamado pelo JwtFilter



    // Verifica se o token é válido para esse usuário
    public boolean tokenValido(String token, Usuario usuario) {
        String email = extrairEmail(token);
        // email do token bate com o usuário E token não expirou
        return email.equals(usuario.getEmail()) && !tokenExpirado(token);
    }

    // Verifica se o token já expirou
    private boolean tokenExpirado(String token) {
        // data de expiração é antes de agora → expirado
        return extrairClaims(token).getExpiration().before(new Date());
    }

    //🔑 Raciocínio:
    //tokenValido verifica 2 coisas:
    //  1. email do token == email do usuário
    //  2. token não expirou
    //
    //As duas precisam ser verdade → AND (&&)
    //Se qualquer uma falhar → token inválido

}
