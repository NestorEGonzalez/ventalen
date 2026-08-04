package com.ventalen.venta;

import com.ventalen.auth.Usuario;
import com.ventalen.cliente.Cliente;
import com.ventalen.detalleVenta.DetalleVentaMapper;
import com.ventalen.detalleVenta.DetalleVentaResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-03T20:06:32-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Debian)"
)
@Component
public class VentaMapperImpl implements VentaMapper {

    @Autowired
    private DetalleVentaMapper detalleVentaMapper;

    @Override
    public VentaResponse toResponse(Venta venta) {
        if ( venta == null ) {
            return null;
        }

        String usuario = null;
        Long clienteId = null;
        String clienteNombre = null;
        Long id = null;
        LocalDate fecha = null;
        Boolean pagado = null;
        List<DetalleVentaResponse> detalles = null;

        usuario = ventaUsuarioUsername( venta );
        clienteId = ventaClienteId( venta );
        clienteNombre = ventaClienteNyap( venta );
        id = venta.getId();
        fecha = venta.getFecha();
        pagado = venta.getPagado();
        detalles = detalleVentaMapper.toResponseList( venta.getDetalles() );

        VentaResponse ventaResponse = new VentaResponse( id, fecha, pagado, usuario, clienteId, clienteNombre, detalles );

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

    private Long ventaClienteId(Venta venta) {
        if ( venta == null ) {
            return null;
        }
        Cliente cliente = venta.getCliente();
        if ( cliente == null ) {
            return null;
        }
        Long id = cliente.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String ventaClienteNyap(Venta venta) {
        if ( venta == null ) {
            return null;
        }
        Cliente cliente = venta.getCliente();
        if ( cliente == null ) {
            return null;
        }
        String nyap = cliente.getNyap();
        if ( nyap == null ) {
            return null;
        }
        return nyap;
    }
}
