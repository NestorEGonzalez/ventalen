package com.ventalen.detalleIngreso;


import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DetalleIngresoMapper {

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    DetalleIngresoResponse toResponse(DetalleIngreso detalle);

    List<DetalleIngresoResponse> toResponseList(List<DetalleIngreso> detalles);
}
