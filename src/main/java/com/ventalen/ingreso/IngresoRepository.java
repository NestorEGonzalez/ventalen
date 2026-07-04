package com.ventalen.ingreso;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ventalen.proveedor.Proveedor;
import com.ventalen.auth.Usuario;

public interface IngresoRepository extends JpaRepository<Ingreso, Long> {

    List<Ingreso> findByProveedor(Proveedor proveedor);

    List<Ingreso> findByUsuario(Usuario usuario);

}
