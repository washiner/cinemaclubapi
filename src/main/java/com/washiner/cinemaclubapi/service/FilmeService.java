package com.washiner.cinemaclubapi.service;

import com.washiner.cinemaclubapi.domain.entity.Filme;
import com.washiner.cinemaclubapi.dto.request.FilmeRequest;
import com.washiner.cinemaclubapi.dto.response.FilmeResponse;
import com.washiner.cinemaclubapi.exception.FilmeNaoEncontradoException;
import com.washiner.cinemaclubapi.mapper.FilmeMapper;
import com.washiner.cinemaclubapi.repository.FilmeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FilmeService {

    private final FilmeRepository filmeRepository;
    private final FilmeMapper filmeMapper;

    // ===== LISTAR =====
    // Só filmes ativos com paginação
    @Transactional(readOnly = true)
    public Page<FilmeResponse> listar(Pageable pageable) {
        return filmeRepository.findByAtivoTrue(pageable)
                .map(filmeMapper::toResponse);
    }

    // ===== BUSCAR POR ID =====
    @Transactional(readOnly = true)
    public FilmeResponse buscarPorId(Long id) {
        return filmeRepository.findById(id)
                .map(filmeMapper::toResponse)
                .orElseThrow(() -> new FilmeNaoEncontradoException(id));
    }

    // ===== BUSCAR POR GÊNERO =====
    @Transactional(readOnly = true)
    public Page<FilmeResponse> buscarPorGenero(String genero, Pageable pageable) {
        return filmeRepository.findByGeneroIgnoreCase(genero, pageable)
                .map(filmeMapper::toResponse);
    }

    // ===== CADASTRAR =====
    // Só ADMIN — controlado pelo SecurityConfig
    @Transactional
    public FilmeResponse cadastrar(FilmeRequest request) {
        Filme filme = filmeMapper.toEntity(request);
        Filme salvo = filmeRepository.save(filme);
        return filmeMapper.toResponse(salvo);
    }

    // ===== ATUALIZAR =====
    @Transactional
    public FilmeResponse atualizar(Long id, FilmeRequest request) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new FilmeNaoEncontradoException(id));

        // Atualiza campos manualmente
        // não usa mapper para não sobrescrever id e criadoEm
        filme.setTitulo(request.titulo());
        filme.setDescricao(request.descricao());
        filme.setGenero(request.genero());
        filme.setAno(request.ano());
        filme.setNota(request.nota());

        return filmeMapper.toResponse(filmeRepository.save(filme));
    }

    // ===== DELETAR =====
    // Deleção lógica — não remove do banco, só marca inativo
    @Transactional
    public void deletar(Long id) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new FilmeNaoEncontradoException(id));
        filme.setAtivo(false);
        filmeRepository.save(filme);
    }
}