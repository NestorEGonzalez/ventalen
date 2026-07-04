package com.ventalen.proveedor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    List<Proveedor> findAllByActivoTrue();
}
