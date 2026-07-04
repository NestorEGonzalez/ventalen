package com.ventalen.ingreso;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IngresoMapper {

    @Mapping(target = "proveedorId", source = "ingreso.proveedor.id")
    @Mapping(target = "proveedorNombre", source = "ingreso.proveedor.proveedor")
    @Mapping(target = "usuario", source = "ingreso.usuario.username")
    IngresoResponse toResponse(Ingreso ingreso);

    List<IngresoResponse> toResponseList(List<Ingreso> ingresos);
}
