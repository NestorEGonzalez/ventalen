package com.ventalen.stock;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ventalen.producto.Producto;

public interface StockRepository extends JpaRepository<Stock, Long> {

    boolean existsByProducto(Producto producto);

    Optional<Stock> findByProducto(Producto producto);

}
