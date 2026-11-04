package com.ventalen.producto;

import com.ventalen.categoria.Categoria;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-25T11:24:54-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12.1 (Debian)"
)
@Component
public class ProductoMapperImpl implements ProductoMapper {

    @Override
    public ProductoResponse toResponse(Producto producto) {
        if ( producto == null ) {
            return null;
        }

        Long categoriaId = null;
        Long id = null;
        String nombre = null;
        BigDecimal precio = null;

        categoriaId = productoCategoriaId( producto );
        id = producto.getId();
        nombre = producto.getNombre();
        precio = producto.getPrecio();

        ProductoResponse productoResponse = new ProductoResponse( id, nombre, precio, categoriaId );

        return productoResponse;
    }

    @Override
    public List<ProductoResponse> toResponseList(List<Producto> productos) {
        if ( productos == null ) {
            return null;
        }

        List<ProductoResponse> list = new ArrayList<ProductoResponse>( productos.size() );
        for ( Producto producto : productos ) {
            list.add( toResponse( producto ) );
        }

        return list;
    }

    private Long productoCategoriaId(Producto producto) {
        if ( producto == null ) {
            return null;
        }
        Categoria categoria = producto.getCategoria();
        if ( categoria == null ) {
            return null;
        }
        Long id = categoria.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
