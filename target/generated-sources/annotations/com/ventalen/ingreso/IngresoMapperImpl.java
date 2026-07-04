package com.ventalen.ingreso;

import com.ventalen.auth.Usuario;
import com.ventalen.proveedor.Proveedor;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class IngresoMapperImpl implements IngresoMapper {

    @Override
    public IngresoResponse toResponse(Ingreso ingreso) {
        if ( ingreso == null ) {
            return null;
        }

        Long proveedorId = null;
        String proveedorNombre = null;
        String usuario = null;
        Long id = null;
        LocalDate fecha = null;
        List<DetalleIngresoResponse> detalles = null;

        proveedorId = ingresoProveedorId( ingreso );
        proveedorNombre = ingresoProveedorProveedor( ingreso );
        usuario = ingresoUsuarioUsername( ingreso );
        id = ingreso.getId();
        fecha = ingreso.getFecha();
        detalles = detalleIngresoListToDetalleIngresoResponseList( ingreso.getDetalles() );

        IngresoResponse ingresoResponse = new IngresoResponse( id, fecha, proveedorId, proveedorNombre, usuario, detalles );

        return ingresoResponse;
    }

    @Override
    public List<IngresoResponse> toResponseList(List<Ingreso> ingresos) {
        if ( ingresos == null ) {
            return null;
        }

        List<IngresoResponse> list = new ArrayList<IngresoResponse>( ingresos.size() );
        for ( Ingreso ingreso : ingresos ) {
            list.add( toResponse( ingreso ) );
        }

        return list;
    }

    private Long ingresoProveedorId(Ingreso ingreso) {
        if ( ingreso == null ) {
            return null;
        }
        Proveedor proveedor = ingreso.getProveedor();
        if ( proveedor == null ) {
            return null;
        }
        Long id = proveedor.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String ingresoProveedorProveedor(Ingreso ingreso) {
        if ( ingreso == null ) {
            return null;
        }
        Proveedor proveedor = ingreso.getProveedor();
        if ( proveedor == null ) {
            return null;
        }
        String proveedor1 = proveedor.getProveedor();
        if ( proveedor1 == null ) {
            return null;
        }
        return proveedor1;
    }

    private String ingresoUsuarioUsername(Ingreso ingreso) {
        if ( ingreso == null ) {
            return null;
        }
        Usuario usuario = ingreso.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        String username = usuario.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }

    protected DetalleIngresoResponse detalleIngresoToDetalleIngresoResponse(DetalleIngreso detalleIngreso) {
        if ( detalleIngreso == null ) {
            return null;
        }

        Long id = null;
        Integer cantidad = null;
        BigDecimal precioMayorista = null;

        id = detalleIngreso.getId();
        cantidad = detalleIngreso.getCantidad();
        precioMayorista = detalleIngreso.getPrecioMayorista();

        Long productoId = null;
        String productoNombre = null;

        DetalleIngresoResponse detalleIngresoResponse = new DetalleIngresoResponse( id, productoId, productoNombre, cantidad, precioMayorista );

        return detalleIngresoResponse;
    }

    protected List<DetalleIngresoResponse> detalleIngresoListToDetalleIngresoResponseList(List<DetalleIngreso> list) {
        if ( list == null ) {
            return null;
        }

        List<DetalleIngresoResponse> list1 = new ArrayList<DetalleIngresoResponse>( list.size() );
        for ( DetalleIngreso detalleIngreso : list ) {
            list1.add( detalleIngresoToDetalleIngresoResponse( detalleIngreso ) );
        }

        return list1;
    }
}
