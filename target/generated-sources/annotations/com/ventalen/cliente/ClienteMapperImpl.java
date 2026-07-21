package com.ventalen.cliente;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-21T15:47:18-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class ClienteMapperImpl implements ClienteMapper {

    @Override
    public ClienteResponse toResponse(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }

        Long id = null;
        String nyap = null;
        String telefono = null;
        Boolean activo = null;

        id = cliente.getId();
        nyap = cliente.getNyap();
        telefono = cliente.getTelefono();
        activo = cliente.getActivo();

        ClienteResponse clienteResponse = new ClienteResponse( id, nyap, telefono, activo );

        return clienteResponse;
    }

    @Override
    public List<ClienteResponse> toResponseList(List<Cliente> clientes) {
        if ( clientes == null ) {
            return null;
        }

        List<ClienteResponse> list = new ArrayList<ClienteResponse>( clientes.size() );
        for ( Cliente cliente : clientes ) {
            list.add( toResponse( cliente ) );
        }

        return list;
    }
}
