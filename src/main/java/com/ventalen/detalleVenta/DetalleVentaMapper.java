package com.ventalen.detalleVenta;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DetalleVentaMapper {

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    DetalleVentaResponse toResponse(DetalleVenta detalle);

    List<DetalleVentaResponse> toResponseList(List<DetalleVenta> detalles);
}
