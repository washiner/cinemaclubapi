package com.washiner.cinemaclubapi.repository;

import com.washiner.cinemaclubapi.domain.entity.RefreshToken;
import com.washiner.cinemaclubapi.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    // @Modifying → avisa que essa query altera dados
    // @Query → escreve o JPQL na mão
    // JPQL usa nome da CLASSE e do CAMPO — não da tabela!
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revogado = true WHERE r.usuario = :usuario")
    void revogarTodosPorUsuario(Usuario usuario);
}


//Método 1 — busca o token pelo valor
//  "Tenho esse cupom aqui: abc-123"
//  "Existe esse cupom no banco?"
//  → findBy + Token
//  → Optional<RefreshToken> findByToken(String token)
//
//Método 2 — revoga todos os tokens de um usuário
//  "Usuário fez logout"
//  "Marca TODOS os tokens dele como revogado = true"
//  → não dá para fazer com findBy — precisa de UPDATE
//  → usa @Query para escrever o JPQL na mão