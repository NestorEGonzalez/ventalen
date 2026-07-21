package com.ventalen.venta;

import com.ventalen.auth.Usuario;
import com.ventalen.detalleVenta.DetalleVenta;
import com.ventalen.detalleVenta.DetalleVentaResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-21T15:47:17-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class VentaMapperImpl implements VentaMapper {

    @Override
    public VentaResponse toResponse(Venta venta) {
        if ( venta == null ) {
            return null;
        }

        String usuario = null;
        Long id = null;
        LocalDate fecha = null;
        Boolean pagado = null;
        List<DetalleVentaResponse> detalles = null;

        usuario = ventaUsuarioUsername( venta );
        id = venta.getId();
        fecha = venta.getFecha();
        pagado = venta.getPagado();
        detalles = detalleVentaListToDetalleVentaResponseList( venta.getDetalles() );

        VentaResponse ventaResponse = new VentaResponse( id, fecha, pagado, usuario, detalles );

        return ventaResponse;
    }

    @Override
    public List<VentaResponse> toResponseList(List<Venta> ventas) {
        if ( ventas == null ) {
            return null;
        }

        List<VentaResponse> list = new ArrayList<VentaResponse>( ventas.size() );
        for ( Venta venta : ventas ) {
            list.add( toResponse( venta ) );
        }

        return list;
    }

    private String ventaUsuarioUsername(Venta venta) {
        if ( venta == null ) {
            return null;
        }
        Usuario usuario = venta.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        String username = usuario.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }

    protected DetalleVentaResponse detalleVentaToDetalleVentaResponse(DetalleVenta detalleVenta) {
        if ( detalleVenta == null ) {
            return null;
        }

        Long id = null;
        Integer cantidad = null;
        BigDecimal precioVenta = null;

        id = detalleVenta.getId();
        cantidad = detalleVenta.getCantidad();
        precioVenta = detalleVenta.getPrecioVenta();

        Long productoId = null;
        String productoNombre = null;

        DetalleVentaResponse detalleVentaResponse = new DetalleVentaResponse( id, productoId, productoNombre, cantidad, precioVenta );

        return detalleVentaResponse;
    }

    protected List<DetalleVentaResponse> detalleVentaListToDetalleVentaResponseList(List<DetalleVenta> list) {
        if ( list == null ) {
            return null;
        }

        List<DetalleVentaResponse> list1 = new ArrayList<DetalleVentaResponse>( list.size() );
        for ( DetalleVenta detalleVenta : list ) {
            list1.add( detalleVentaToDetalleVentaResponse( detalleVenta ) );
        }

        return list1;
    }
}
