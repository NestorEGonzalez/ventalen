package com.ventalen.detalleIngreso;

import com.ventalen.producto.Producto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-13T12:30:24-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class DetalleIngresoMapperImpl implements DetalleIngresoMapper {

    @Override
    public DetalleIngresoResponse toResponse(DetalleIngreso detalle) {
        if ( detalle == null ) {
            return null;
        }

        Long productoId = null;
        String productoNombre = null;
        Long id = null;
        Integer cantidad = null;
        BigDecimal precioMayorista = null;

        productoId = detalleProductoId( detalle );
        productoNombre = detalleProductoNombre( detalle );
        id = detalle.getId();
        cantidad = detalle.getCantidad();
        precioMayorista = detalle.getPrecioMayorista();

        DetalleIngresoResponse detalleIngresoResponse = new DetalleIngresoResponse( id, productoId, productoNombre, cantidad, precioMayorista );

        return detalleIngresoResponse;
    }

    @Override
    public List<DetalleIngresoResponse> toResponseList(List<DetalleIngreso> detalles) {
        if ( detalles == null ) {
            return null;
        }

        List<DetalleIngresoResponse> list = new ArrayList<DetalleIngresoResponse>( detalles.size() );
        for ( DetalleIngreso detalleIngreso : detalles ) {
            list.add( toResponse( detalleIngreso ) );
        }

        return list;
    }

    private Long detalleProductoId(DetalleIngreso detalleIngreso) {
        if ( detalleIngreso == null ) {
            return null;
        }
        Producto producto = detalleIngreso.getProducto();
        if ( producto == null ) {
            return null;
        }
        Long id = producto.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String detalleProductoNombre(DetalleIngreso detalleIngreso) {
        if ( detalleIngreso == null ) {
            return null;
        }
        Producto producto = detalleIngreso.getProducto();
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
