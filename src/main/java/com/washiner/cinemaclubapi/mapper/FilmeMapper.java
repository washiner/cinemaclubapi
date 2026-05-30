package com.washiner.cinemaclubapi.mapper;

import com.washiner.cinemaclubapi.domain.entity.Filme;
import com.washiner.cinemaclubapi.dto.request.FilmeRequest;
import com.washiner.cinemaclubapi.dto.response.FilmeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FilmeMapper {

    FilmeResponse toResponse(Filme filme);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    Filme toEntity(FilmeRequest request);
}