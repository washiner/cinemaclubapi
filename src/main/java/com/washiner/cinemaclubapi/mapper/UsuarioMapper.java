package com.washiner.cinemaclubapi.mapper;

import com.washiner.cinemaclubapi.domain.entity.Usuario;
import com.washiner.cinemaclubapi.dto.response.UsuarioResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioResponse toResponse(Usuario usuario);
}
