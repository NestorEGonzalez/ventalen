package com.ventalen.venta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ventalen.auth.Usuario;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByUsuario(Usuario usuario);

    List<Venta> findByPagado(Boolean pagado);

}
