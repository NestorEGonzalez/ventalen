package com.ventalen.proveedor;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-21T15:47:19-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class ProveedorMapperImpl implements ProveedorMapper {

    @Override
    public ProveedorResponse toResponse(Proveedor proveedor) {
        if ( proveedor == null ) {
            return null;
        }

        Long id = null;
        String proveedor1 = null;
        String corredor = null;
        String telefono = null;

        id = proveedor.getId();
        proveedor1 = proveedor.getProveedor();
        corredor = proveedor.getCorredor();
        telefono = proveedor.getTelefono();

        ProveedorResponse proveedorResponse = new ProveedorResponse( id, proveedor1, corredor, telefono );

        return proveedorResponse;
    }

    @Override
    public List<ProveedorResponse> toResponseList(List<Proveedor> proveedores) {
        if ( proveedores == null ) {
            return null;
        }

        List<ProveedorResponse> list = new ArrayList<ProveedorResponse>( proveedores.size() );
        for ( Proveedor proveedor : proveedores ) {
            list.add( toResponse( proveedor ) );
        }

        return list;
    }
}
