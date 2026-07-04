package com.ventalen.ingreso;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ventalen.producto.Producto;

public interface DetalleIngresoRepository extends JpaRepository<DetalleIngreso, Long> {

    List<DetalleIngreso> findByIngresoId(Long ingresoId);

    List<DetalleIngreso> findByProducto(Producto producto);

}
