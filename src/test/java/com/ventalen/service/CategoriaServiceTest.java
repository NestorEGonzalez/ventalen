package com.ventalen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaRepository;
import com.ventalen.categoria.CategoriaService;
import com.ventalen.exception.ErrorCategoriaInexistente;
import com.ventalen.exception.ErrorCategoriaYaExistente;

@ExtendWith(MockitoExtension.class)

public class CategoriaServiceTest{
    
    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    String nombreCategoria;
    Categoria categoria;
    Long id;
    String nombreNuevo;

    @BeforeEach
    void setUp(){
        nombreCategoria = "Prueba";
        categoria = new Categoria(nombreCategoria);
        id = 1L;
        nombreNuevo = "Nuevo";
    }


    @Test
    void test01_sePuedeGuardarUnaCategoriaYSeRetonarLaCategoriaGuardada(){
        when(categoriaRepository.existsByCategoria(nombreCategoria.trim().toLowerCase())).thenReturn(false);

        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        Categoria categoriaDelService = categoriaService.crearCategoria(nombreCategoria);

        assertNotNull(categoriaDelService);
        assertEquals(nombreCategoria.trim().toLowerCase(), categoriaDelService.getCategoria());

        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void test02_alGuardarUnaCategoriaExistenteSeLanzaUnError(){
        when(categoriaRepository.existsByCategoria(nombreCategoria.trim().toLowerCase())).thenReturn(true);

        assertThrows(ErrorCategoriaYaExistente.class,()->{
            categoriaService.crearCategoria(nombreCategoria);
        });

    }

    @Test
    void test03_sePuedeObtenerUnaCategoriaPorSuNombre(){
        when(categoriaRepository.findOneByCategoriaIgnoreCase(nombreCategoria)).thenReturn(Optional.of(categoria));
    
        Optional<Categoria> catEncontrada = categoriaService.buscarCategoriaPorNombre(nombreCategoria);

        assertNotNull(catEncontrada);
        assertEquals(nombreCategoria.trim().toLowerCase(),catEncontrada.get().getCategoria());
        
    }

    @Test
    void test04_siElNombreDeUnaCategoriaNoExisteObtengoNull(){        
        Optional<Categoria> catEncontrada = categoriaService.buscarCategoriaPorNombre(nombreCategoria);

        assertFalse(catEncontrada.isPresent());
    }

    @Test
    void test05_sePuedeBorrarUnaCategoriaExistente(){
    when(categoriaRepository.existsById(id)).thenReturn(true);

        categoriaService.eliminarCategoria(id);

        verify(categoriaRepository).deleteById(id);;
        
    }

    @Test
    void test06_noSePuedeBorrarUnaCategoriaInexistente(){
        when(categoriaRepository.existsById(id)).thenReturn(false);

        assertThrows(ErrorCategoriaInexistente.class, ()->{
            categoriaService.eliminarCategoria(id);
        });

        verify(categoriaRepository, never()).delete(any());

    }

    @Test
    void test07_sePuedeModificarElNombreDeUnaCategoriaExistente(){        
        when(categoriaRepository.findOneByCategoriaIgnoreCase(nombreCategoria)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByCategoria(nombreNuevo.trim().toLowerCase())).thenReturn(false);

        categoriaService.cambiarNombreDeCategoria(nombreCategoria,nombreNuevo);

        assertEquals(nombreNuevo.trim().toLowerCase(), categoria.getCategoria());

        verify(categoriaRepository).save(categoria);

    }

    @Test
    void test08_noSePuedeModificarElNombreDeUnaCategoriaExistenteSiElNombreYaExiste(){
        nombreNuevo = nombreCategoria;
        when(categoriaRepository.existsByCategoria(nombreCategoria.trim().toLowerCase())).thenReturn(true);
        when(categoriaRepository.existsByCategoria(nombreNuevo.trim().toLowerCase())).thenReturn(true);

        assertThrows(RuntimeException.class,()->{
            categoriaService.cambiarNombreDeCategoria(nombreCategoria, nombreNuevo);
        });

        verify(categoriaRepository,never()).save(any());
    }

    @Test
    void test09_noSePuedeModificarElNombreDeUnaCategoriaQueNoExiste(){
        when(categoriaRepository.findOneByCategoriaIgnoreCase(nombreCategoria)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, ()->{
            categoriaService.cambiarNombreDeCategoria(nombreCategoria, nombreNuevo);
        });

        verify(categoriaRepository, never()).save(any());
    }

}