package com.ventalen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ventalen.TestBase;
import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaService;
import com.ventalen.exception.ErrorCategoriaInexistente;
import com.ventalen.exception.ErrorCategoriaYaExistente;
import com.ventalen.exception.ErrorNombreDeCategoriaInexistente;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional

public class CategoriaServiceTestIT extends TestBase {

    @Autowired 
    CategoriaService categoriaService;

    String nombreCategoria;

    @BeforeEach
    void setUp(){
        nombreCategoria = "Prueba";
    }

    @Test
    void test01_seCreaUnaCategoriaYSeGuardaEnElRepositorio(){
        
        categoriaService.crearCategoria(nombreCategoria);

        assertEquals(1, categoriaService.obtenerTodasLasCategorias().size());
    }

    @Test
    void test02_noSeGuardaUnaCategoriaQueYaExiste(){
        categoriaService.crearCategoria(nombreCategoria);
    
        String mensajeEsperado = "La categoria "+ nombreCategoria.trim().toLowerCase() + ", ya existe.";
    
        Exception categoriaExistente = assertThrows(ErrorCategoriaYaExistente.class,()->{
            categoriaService.crearCategoria(nombreCategoria);
        });

        assertEquals(mensajeEsperado, categoriaExistente.getMessage());
            
    }

    @Test
    void test03_sePuedeBorrarUnaCategoriaExistente(){
        Categoria cat = categoriaService.crearCategoria(nombreCategoria);
        
        assertEquals(1,categoriaService.obtenerTodasLasCategorias().size());

        categoriaService.eliminarCategoria(cat.getId());

        assertEquals(0,categoriaService.obtenerTodasLasCategorias().size());
    }

    @Test
    void test04_noSePuedeBorrarUnaCategoriaInexistente(){
        Long id = 1L;
        String mensajeEsperado = "La categoria con id: "+id+", no existe.";
    
        assertEquals(0,categoriaService.obtenerTodasLasCategorias().size());
        
        Exception catInexistente = assertThrows(ErrorCategoriaInexistente.class, ()->{
            categoriaService.eliminarCategoria(id);
        });

        assertEquals(mensajeEsperado,catInexistente.getMessage());
        assertEquals(0,categoriaService.obtenerTodasLasCategorias().size());
    }

    @Test
    void test05_sePuedeModificarUnaCategoriaExistente(){
        Categoria cat = categoriaService.crearCategoria(nombreCategoria);
    
        assertEquals(1,categoriaService.obtenerTodasLasCategorias().size());

        String nombreNuevo = "Nuevo";
        categoriaService.cambiarNombreDeCategoria(nombreCategoria, nombreNuevo);

        assertEquals(cat.getId(), categoriaService.buscarCategoriaPorNombre(nombreNuevo).get().getId());
        assertEquals(1,categoriaService.obtenerTodasLasCategorias().size());
    }

    @Test
    void test06_noSePuedeModificarUnaCategoriaAsignandoleUnNombreExistente(){
        categoriaService.crearCategoria(nombreCategoria);
        
        assertEquals(1,categoriaService.obtenerTodasLasCategorias().size());

        String nombre = nombreCategoria;
        String mensajeEsperado = "La categoria "+ nombre.trim().toLowerCase() + ", ya existe.";
        
        Exception errorCatExistente = assertThrows(ErrorCategoriaYaExistente.class, ()->{
            categoriaService.cambiarNombreDeCategoria(nombreCategoria, nombre);
        });
        
        assertEquals(mensajeEsperado, errorCatExistente.getMessage());
        assertEquals(1,categoriaService.obtenerTodasLasCategorias().size());
    }

    @Test
    void test07_noSePuedeModificarUnaCategoriaInexistente(){
        String nombre = "No existe";
        String mensajeEsperado = "La categoria: "+nombre+", no existe.";

        Exception errorInexistente = assertThrows(ErrorNombreDeCategoriaInexistente.class,()->{
            categoriaService.cambiarNombreDeCategoria(nombre, nombreCategoria);
        });

        assertEquals(mensajeEsperado, errorInexistente.getMessage());
        assertEquals(0,categoriaService.obtenerTodasLasCategorias().size());
    }
    
}
