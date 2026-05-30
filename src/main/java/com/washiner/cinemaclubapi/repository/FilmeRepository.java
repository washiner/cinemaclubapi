package com.washiner.cinemaclubapi.repository;

import com.washiner.cinemaclubapi.domain.entity.Filme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmeRepository extends JpaRepository<Filme, Long> {

    Page<Filme> findByAtivoTrue(Pageable pageable);

    Page<Filme> findByGeneroIgnoreCase(String genero, Pageable pageable);
}
