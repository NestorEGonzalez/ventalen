package com.ventalen.ajuste;

import com.ventalen.auth.Usuario;
import com.ventalen.motivo.Motivo;
import com.ventalen.producto.Producto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-04T17:03:44-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class AjusteStockMapperImpl implements AjusteStockMapper {

    @Override
    public AjusteStockResponse toResponse(AjusteStock ajuste) {
        if ( ajuste == null ) {
            return null;
        }

        Long productoId = null;
        String productoNombre = null;
        Long motivoId = null;
        String motivoNombre = null;
        Boolean afectaPositivo = null;
        String usuario = null;
        Long id = null;
        LocalDate fecha = null;
        Integer cantidad = null;
        String detalles = null;

        productoId = ajusteProductoId( ajuste );
        productoNombre = ajusteProductoNombre( ajuste );
        motivoId = ajusteMotivoId( ajuste );
        motivoNombre = ajusteMotivoMotivo( ajuste );
        afectaPositivo = ajusteMotivoAfectaPositivo( ajuste );
        usuario = ajusteUsuarioUsername( ajuste );
        id = ajuste.getId();
        fecha = ajuste.getFecha();
        cantidad = ajuste.getCantidad();
        detalles = ajuste.getDetalles();

        AjusteStockResponse ajusteStockResponse = new AjusteStockResponse( id, fecha, productoId, productoNombre, cantidad, motivoId, motivoNombre, afectaPositivo, usuario, detalles );

        return ajusteStockResponse;
    }

    @Override
    public List<AjusteStockResponse> toResponseList(List<AjusteStock> ajustes) {
        if ( ajustes == null ) {
            return null;
        }

        List<AjusteStockResponse> list = new ArrayList<AjusteStockResponse>( ajustes.size() );
        for ( AjusteStock ajusteStock : ajustes ) {
            list.add( toResponse( ajusteStock ) );
        }

        return list;
    }

    private Long ajusteProductoId(AjusteStock ajusteStock) {
        if ( ajusteStock == null ) {
            return null;
        }
        Producto producto = ajusteStock.getProducto();
        if ( producto == null ) {
            return null;
        }
        Long id = producto.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String ajusteProductoNombre(AjusteStock ajusteStock) {
        if ( ajusteStock == null ) {
            return null;
        }
        Producto producto = ajusteStock.getProducto();
        if ( producto == null ) {
            return null;
        }
        String nombre = producto.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    private Long ajusteMotivoId(AjusteStock ajusteStock) {
        if ( ajusteStock == null ) {
            return null;
        }
        Motivo motivo = ajusteStock.getMotivo();
        if ( motivo == null ) {
            return null;
        }
        Long id = motivo.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String ajusteMotivoMotivo(AjusteStock ajusteStock) {
        if ( ajusteStock == null ) {
            return null;
        }
        Motivo motivo = ajusteStock.getMotivo();
        if ( motivo == null ) {
            return null;
        }
        String motivo1 = motivo.getMotivo();
        if ( motivo1 == null ) {
            return null;
        }
        return motivo1;
    }

    private Boolean ajusteMotivoAfectaPositivo(AjusteStock ajusteStock) {
        if ( ajusteStock == null ) {
            return null;
        }
        Motivo motivo = ajusteStock.getMotivo();
        if ( motivo == null ) {
            return null;
        }
        Boolean afectaPositivo = motivo.getAfectaPositivo();
        if ( afectaPositivo == null ) {
            return null;
        }
        return afectaPositivo;
    }

    private String ajusteUsuarioUsername(AjusteStock ajusteStock) {
        if ( ajusteStock == null ) {
            return null;
        }
        Usuario usuario = ajusteStock.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        String username = usuario.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }
}
