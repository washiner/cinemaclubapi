package com.washiner.cinemaclubapi.repository;

import com.washiner.cinemaclubapi.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // findBy   → retorna o objeto
    Optional<Usuario> findByEmail(String email);

    // existsBy → retorna true ou false
    boolean existsByEmail(String email);
}

//Regra simples para gravar:
//
//findBy___   → Optional<Entidade>  → pode achar ou não
//existsBy___ → boolean             → sim ou não
//countBy___  → long                → quantos?
//deleteBy___ → void                → deleta
//existsBy não precisa de Optional porque a resposta já é simples: true ou false — nunca nulo.