package com.ventalen.categoria;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional <Categoria> findOneByCategoria(String nombreCategoria);

    boolean existsByCategoria(String nombre);

    Optional<Categoria> findOneByCategoriaIgnoreCase(String nombreCategoria);

    List<Categoria> findAllByCategoriaContainingIgnoreCase(String cat);

    
} 
