package com.ventalen.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import com.ventalen.TestBase;
import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaRepository;
import com.ventalen.exception.ErrorCampoVacioONulo;

import jakarta.transaction.Transactional;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)


public class CategoriaRepositoryTest extends TestBase{
    
    @Autowired
    CategoriaRepository categoriaRepository;

    String nombreCategoria;
    Categoria categoria;

    @BeforeEach
    void setUp(){
        nombreCategoria = "Prueba";
        categoria = new Categoria(nombreCategoria);
        
    }
 
    @Test
    void test01_sePuedeCrearYGuardarUnaCategoria(){
        Categoria categoriaGuardada = categoriaRepository.save(categoria);

        assertNotNull(categoriaGuardada);
        assertEquals(categoriaRepository.count(),1);
    }

    @Test
    void test02_sePuedeEliminarUnaCategoria(){
        Categoria categoriaGuardada = categoriaRepository.save(categoria);

        assertNotNull(categoriaGuardada);
        assertEquals(categoriaRepository.count(),1);

        categoriaRepository.deleteById(categoriaGuardada.getId());
        assertEquals(categoriaRepository.count(),0);

    }

    @Test
    void test03_sePuedeModificarElNombreDeUnaCategoria(){
        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        
        categoriaGuardada.setCategoria("Nuevo");
        Categoria categoriaModificada = categoriaRepository.save(categoriaGuardada);
        assertEquals(categoriaModificada.getCategoria(), categoriaRepository.findById(categoriaGuardada.getId()).get().getCategoria());

    }

    @Test
    void test04_sePuedeBuscarUnaCategoriaPorNombreYObtenerSuId(){
        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        
        Categoria categoriaEncontrada = categoriaRepository.findOneByCategoriaIgnoreCase(nombreCategoria).get();
        assertEquals(categoriaGuardada.getId(), categoriaEncontrada.getId());
    }

    @Test
    void test05_noPuedenExistirDosCategoriasConElMismoNombre(){
        categoriaRepository.save(categoria);
        
        assertThrows(DataIntegrityViolationException.class, ()->{
            categoriaRepository.saveAndFlush(new Categoria(nombreCategoria));
        });

    }

    @Test
    void test06_noSePuedeGuardarUnaCategoriaConNombreNuloOVacio(){
        Exception nombreNulo = assertThrows(ErrorCampoVacioONulo.class, ()->{
            new Categoria(null);
        });

        Exception nombreVacio = assertThrows(ErrorCampoVacioONulo.class,()->{
            new Categoria("");
        });

        assertEquals(nombreNulo.getMessage(), ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO);
        assertEquals(nombreVacio.getMessage(), ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO);

    }

    @Test
    void test07_sePuedenObtenerTodasLasCategorias(){
        Categoria cat = new Categoria("Otra");
        categoriaRepository.save(cat);
        categoriaRepository.save(categoria);
        
        List<Categoria> todasLasCategorias = categoriaRepository.findAll();
        
        assertEquals(2, todasLasCategorias.size());
    }
}
