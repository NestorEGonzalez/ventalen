package com.ventalen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaRepository;
import com.ventalen.categoria.CategoriaService;
import com.ventalen.exception.ErrorCategoriaInexistente;
import com.ventalen.exception.ErrorNombreProductoExistente;
import com.ventalen.exception.ErrorProductoConIdInexistente;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoRepository;
import com.ventalen.producto.ProductoService;

@ExtendWith(MockitoExtension.class)

public class ProductoServiceTest {

    @InjectMocks
    ProductoService productoService;
    
    @InjectMocks
    CategoriaService categoriaService;
    
    @Mock
    ProductoRepository productoRepository;
    
    @Mock
    CategoriaRepository categoriaRepository;

    Long idCat;
    String nombreCat;
    Categoria cat;
    String nombreProd;
    BigDecimal precio;

    @BeforeEach
    void setUp(){
        idCat = 1L;
        nombreCat = "Categoria";
        cat = new Categoria(nombreCat);
        nombreProd = "Producto";
        precio = new BigDecimal(100);
    }

    @Test
    void test01_sePuedeCrearUnProducto(){
        
        when(categoriaRepository.findById(idCat)).thenReturn(Optional.of(cat));

        Producto producto = new Producto(nombreProd,precio,cat);
        
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        
        Producto prodDelService = productoService.crearProducto(nombreProd,precio,idCat);

        assertNotNull(prodDelService);
        assertEquals(nombreProd.trim().toLowerCase(), prodDelService.getNombre());
        assertEquals(precio, prodDelService.getPrecio());
        assertEquals(nombreCat.trim().toLowerCase(), prodDelService.getCategoria().getCategoria());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void test_noSePuedeCrearUnProductoSinCategoria(){
        
        when(categoriaRepository.findById(idCat)).thenReturn(Optional.empty());

        assertThrows(ErrorCategoriaInexistente.class, ()->{
            productoService.crearProducto(nombreProd, precio, idCat);
        });
    }

    @Test
    void test_SePuedeBorrarUnProducto(){
        
        Long idProd = 1L;

        when(productoRepository.existsById(idProd)).thenReturn(true);

        productoService.borrarProductoConId(idProd);

        verify(productoRepository).deleteById(idProd);


    }

    @Test
    void test_NoSePuedeBorrarUnProductoQueNoExiste(){
        Long idProd = 1L;
        
        when(productoRepository.existsById(idProd)).thenReturn(false);

        assertThrows(ErrorProductoConIdInexistente.class,()->{
            productoService.borrarProductoConId(idProd);
        });

        verify(productoRepository,never()).deleteById(any());

    }

    @Test
    void test_SePuedeCambiarElPrecioOCategoriaONombreDeUnProducto__(){
    
    Long idProd = 1L;
    Long idCatNueva = 2L;
           
    String nombreCatNuevo = "Nueva";
    Categoria catNueva = new Categoria(nombreCatNuevo);

    Producto producto = new Producto("Viejo", new BigDecimal("50"), cat);

    String nombreNuevo = "Nuevo";
    BigDecimal precioNuevo = new BigDecimal("5000");


    when(productoRepository.findById(idProd)).thenReturn(Optional.of(producto));

    when(categoriaRepository.findById(idCatNueva)).thenReturn(Optional.of(catNueva));

    when(productoRepository.existsByNombre(nombreNuevo.trim().toLowerCase())).thenReturn(false);


    productoService.cambiarNombreDeProducto(idProd, nombreNuevo);
    productoService.cambiarPrecioDeProducto(idProd, precioNuevo);
    productoService.cambiarCategoriaDeProducto(idProd, idCatNueva);

    assertEquals(nombreNuevo.trim().toLowerCase(), producto.getNombre());
    assertEquals(precioNuevo, producto.getPrecio());
    assertEquals(nombreCatNuevo.trim().toLowerCase(), producto.getCategoria().getCategoria());

    
    verify(productoRepository, atLeastOnce()).save(producto);
    }

    @Test
    void test_NoSePuedeCambiarElNombreDeUnProductoSiYaExisteOtroNombreIgual(){
        when(categoriaRepository.findById(idCat)).thenReturn(Optional.of(cat));

        Producto producto = new Producto(nombreProd,precio,cat);
        
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        
        Producto prodDelService = productoService.crearProducto(nombreProd,precio,idCat);
        
        assertEquals(nombreProd.toLowerCase(), prodDelService.getNombre());

        Long idProd = 1L;

        String nombreNuevo = nombreProd;

        when(productoRepository.findById(idProd)).thenReturn(Optional.of(prodDelService));

        when(productoRepository.existsByNombre(nombreNuevo.trim().toLowerCase())).thenReturn(true);

        assertThrows(ErrorNombreProductoExistente.class,()->{
            productoService.cambiarNombreDeProducto(idProd, nombreNuevo);
        });

    }

    @Test
    void test_NoSePuedeCambiarLaCategoriaSiNoExisteLaCategoria(){
        when(categoriaRepository.findById(idCat)).thenReturn(Optional.of(cat));

        Producto producto = new Producto(nombreProd,precio,cat);
        
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        
        Producto prodDelService = productoService.crearProducto(nombreProd,precio,idCat);

        Long catInexistente = 99L;

        Long idProd = 1L;

        when(categoriaRepository.findById(catInexistente)).thenReturn(Optional.empty());

        when(productoRepository.findById(idProd)).thenReturn(Optional.of(prodDelService));

        assertThrows(ErrorCategoriaInexistente.class, ()->{
            productoService.cambiarCategoriaDeProducto(idProd, catInexistente);
        });

    }
}
