package com.ventalen.ingreso;

import com.ventalen.auth.Usuario;
import com.ventalen.detalleIngreso.DetalleIngresoMapper;
import com.ventalen.detalleIngreso.DetalleIngresoResponse;
import com.ventalen.proveedor.Proveedor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-13T12:30:24-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class IngresoMapperImpl implements IngresoMapper {

    @Autowired
    private DetalleIngresoMapper detalleIngresoMapper;

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
        detalles = detalleIngresoMapper.toResponseList( ingreso.getDetalles() );

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
}
