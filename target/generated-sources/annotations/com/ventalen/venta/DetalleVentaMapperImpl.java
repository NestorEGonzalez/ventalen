package com.ventalen.venta;

import com.ventalen.producto.Producto;
import java.math.BigDecimal;
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
public class DetalleVentaMapperImpl implements DetalleVentaMapper {

    @Override
    public DetalleVentaResponse toResponse(DetalleVenta detalle) {
        if ( detalle == null ) {
            return null;
        }

        Long productoId = null;
        String productoNombre = null;
        Long id = null;
        Integer cantidad = null;
        BigDecimal precioVenta = null;

        productoId = detalleProductoId( detalle );
        productoNombre = detalleProductoNombre( detalle );
        id = detalle.getId();
        cantidad = detalle.getCantidad();
        precioVenta = detalle.getPrecioVenta();

        DetalleVentaResponse detalleVentaResponse = new DetalleVentaResponse( id, productoId, productoNombre, cantidad, precioVenta );

        return detalleVentaResponse;
    }

    @Override
    public List<DetalleVentaResponse> toResponseList(List<DetalleVenta> detalles) {
        if ( detalles == null ) {
            return null;
        }

        List<DetalleVentaResponse> list = new ArrayList<DetalleVentaResponse>( detalles.size() );
        for ( DetalleVenta detalleVenta : detalles ) {
            list.add( toResponse( detalleVenta ) );
        }

        return list;
    }

    private Long detalleProductoId(DetalleVenta detalleVenta) {
        if ( detalleVenta == null ) {
            return null;
        }
        Producto producto = detalleVenta.getProducto();
        if ( producto == null ) {
            return null;
        }
        Long id = producto.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String detalleProductoNombre(DetalleVenta detalleVenta) {
        if ( detalleVenta == null ) {
            return null;
        }
        Producto producto = detalleVenta.getProducto();
        if ( producto == null ) {
            return null;
        }
        String nombre = producto.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }
}
