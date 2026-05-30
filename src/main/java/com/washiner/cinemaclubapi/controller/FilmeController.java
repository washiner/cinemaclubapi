package com.washiner.cinemaclubapi.controller;

import com.washiner.cinemaclubapi.dto.request.FilmeRequest;
import com.washiner.cinemaclubapi.dto.response.FilmeResponse;
import com.washiner.cinemaclubapi.service.FilmeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/filmes")
@RequiredArgsConstructor
@Tag(name = "Filmes", description = "Gerenciamento do catálogo de filmes")
@SecurityRequirement(name = "bearerAuth")
public class FilmeController {

    private final FilmeService filmeService;

    @GetMapping
    @Operation(summary = "Listar filmes ativos — USER e ADMIN")
    public Page<FilmeResponse> listar(
            @PageableDefault(size = 10, sort = "titulo") Pageable pageable) {
        return filmeService.listar(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar filme por id — USER e ADMIN")
    public FilmeResponse buscarPorId(@PathVariable Long id) {
        return filmeService.buscarPorId(id);
    }

    @GetMapping("/genero/{genero}")
    @Operation(summary = "Buscar filmes por gênero — USER e ADMIN")
    public Page<FilmeResponse> buscarPorGenero(
            @PathVariable String genero,
            @PageableDefault(size = 10) Pageable pageable) {
        return filmeService.buscarPorGenero(genero, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar filme — só ADMIN")
    public FilmeResponse cadastrar(@RequestBody @Valid FilmeRequest request) {
        return filmeService.cadastrar(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar filme — só ADMIN")
    public FilmeResponse atualizar(
            @PathVariable Long id,
            @RequestBody @Valid FilmeRequest request) {
        return filmeService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desativar filme — só ADMIN")
    public void deletar(@PathVariable Long id) {
        filmeService.deletar(id);
    }
}