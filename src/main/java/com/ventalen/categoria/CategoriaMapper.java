package com.ventalen.categoria;

import java.util.List;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    CategoriaResponse toResponse(Categoria categoria);

    List<CategoriaResponse> toResponse(List<Categoria> categorias);
} 
