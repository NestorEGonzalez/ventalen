package com.ventalen.categoria;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-04T17:03:43-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class CategoriaMapperImpl implements CategoriaMapper {

    @Override
    public CategoriaResponse toResponse(Categoria categoria) {
        if ( categoria == null ) {
            return null;
        }

        Long id = null;
        String categoria1 = null;

        id = categoria.getId();
        categoria1 = categoria.getCategoria();

        CategoriaResponse categoriaResponse = new CategoriaResponse( id, categoria1 );

        return categoriaResponse;
    }

    @Override
    public List<CategoriaResponse> toResponse(List<Categoria> categorias) {
        if ( categorias == null ) {
            return null;
        }

        List<CategoriaResponse> list = new ArrayList<CategoriaResponse>( categorias.size() );
        for ( Categoria categoria : categorias ) {
            list.add( toResponse( categoria ) );
        }

        return list;
    }
}
