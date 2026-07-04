package com.ventalen.ajuste;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AjusteStockMapper {

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    @Mapping(target = "motivoId", source = "motivo.id")
    @Mapping(target = "motivoNombre", source = "motivo.motivo")
    @Mapping(target = "afectaPositivo", source = "motivo.afectaPositivo")
    @Mapping(target = "usuario", source = "usuario.username")
    AjusteStockResponse toResponse(AjusteStock ajuste);

    List<AjusteStockResponse> toResponseList(List<AjusteStock> ajustes);
}
