package com.ventalen.proveedor;

import java.util.List;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProveedorMapper {

    ProveedorResponse toResponse(Proveedor proveedor);

    List<ProveedorResponse> toResponseList(List<Proveedor> proveedores);
}
