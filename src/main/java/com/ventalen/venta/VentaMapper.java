package com.ventalen.venta;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ventalen.detalleVenta.DetalleVentaMapper;

@Mapper(componentModel = "spring", uses = DetalleVentaMapper.class)
public interface VentaMapper {

    @Mapping(target = "usuario", source = "venta.usuario.username")
    @Mapping(target = "clienteId", source = "venta.cliente.id")
    @Mapping(target = "clienteNombre", source = "venta.cliente.nyap")
    VentaResponse toResponse(Venta venta);

    List<VentaResponse> toResponseList(List<Venta> ventas);
}
