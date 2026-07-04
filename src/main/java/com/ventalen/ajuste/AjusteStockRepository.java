package com.ventalen.ajuste;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ventalen.producto.Producto;

public interface AjusteStockRepository extends JpaRepository<AjusteStock, Long> {

    List<AjusteStock> findByProducto(Producto producto);

}
