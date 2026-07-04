package com.ventalen.venta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ventalen.producto.Producto;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByVentaId(Long ventaId);

    List<DetalleVenta> findByProducto(Producto producto);

}
