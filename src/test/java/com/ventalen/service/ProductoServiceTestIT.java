package com.ventalen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ventalen.TestBase;
import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaService;
import com.ventalen.exception.ErrorCampoVacioONulo;
import com.ventalen.exception.ErrorCategoriaInexistente;
import com.ventalen.exception.ErrorPrecioInvalido;
import com.ventalen.exception.ErrorProductoConIdInexistente;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional

public class ProductoServiceTestIT extends TestBase{
    
    @Autowired
    ProductoService productoService;

    @Autowired
    CategoriaService categoriaService;

    String nombreProd;
    BigDecimal precioProd;
    String nombreCat;
    Categoria cat;

    @BeforeEach
    void setUp(){
        nombreProd = "Nombre producto";
        precioProd = new BigDecimal(100);
        nombreCat = "Categoria";
        cat = categoriaService.crearCategoria(nombreCat);

    }

    @Test
    void test_SeCreaUnProductoYSeGuardaCorrectamente(){
        Producto prod = productoService.crearProducto(nombreProd, precioProd, cat.getId());

        assertEquals(1, productoService.obtenerTodosLosProductos().size());
        assertNotNull(prod);
        assertEquals(cat.getCategoria(), productoService.buscarProductoConId(prod.getId()).getCategoria().getCategoria());
        assertEquals(precioProd,  productoService.buscarProductoConId(prod.getId()).getPrecio());
        assertEquals(nombreProd.trim().toLowerCase(),  productoService.buscarProductoConId(prod.getId()).getNombre());
        
    }

    @Test
    void test_NoSeGuardaUnProductoSiNoSeCreaCorrectamente(){    
        Exception errorCategoriaInexistente = assertThrows(ErrorCategoriaInexistente.class,()->{
            productoService.crearProducto(nombreProd, precioProd, 99L);
        });

        assertEquals("La categoria con id: 99, no existe.", errorCategoriaInexistente.getMessage());

        Exception errorPrecioNegativo = assertThrows(ErrorPrecioInvalido.class,()->{
            productoService.crearProducto(nombreProd, (new BigDecimal(-1)), cat.getId());
        });

        Exception errorPrecioNulo = assertThrows(ErrorPrecioInvalido.class, ()->{
            productoService.crearProducto(nombreProd, null, cat.getId());
        });

        assertEquals(ErrorPrecioInvalido.ERROR_PRECIO_NEGATIVO, errorPrecioNegativo.getMessage());
        assertEquals(ErrorPrecioInvalido.ERROR_PRECIO_NULO, errorPrecioNulo.getMessage());

        Exception errorNombreNulo = assertThrows(ErrorCampoVacioONulo.class,()->{
            productoService.crearProducto(null, precioProd, cat.getId());
        });

        Exception errorNombreVacio = assertThrows(ErrorCampoVacioONulo.class, ()->{
            productoService.crearProducto("", precioProd, cat.getId());
        });

        assertEquals(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO, errorNombreNulo.getMessage());
        assertEquals(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO, errorNombreVacio.getMessage());

        assertEquals(0, productoService.obtenerTodosLosProductos().size());
    }

    @Test
    void test_sePuedeEliminarUnProductoGuardado(){
        Producto prod = productoService.crearProducto(nombreProd, precioProd, cat.getId());

        assertEquals(1, productoService.obtenerTodosLosProductos().size());

        productoService.borrarProductoConId(prod.getId());

        assertEquals(0, productoService.obtenerTodosLosProductos().size());
        
    }

    @Test
    void test_noSePuedeEliminarUnProductoInexistente(){
        assertEquals(0, productoService.obtenerTodosLosProductos().size());

        Exception errorProductoInexistente = assertThrows(ErrorProductoConIdInexistente.class, ()->{
            productoService.borrarProductoConId(99L);
        });

        assertEquals("El producto con id 99, no existe.", errorProductoInexistente.getMessage());
    }

    @Test
    void test_sePuedeEditarElNombreOPrecioOCategoriaDelProducto(){
        Producto prod = productoService.crearProducto(nombreProd, precioProd, cat.getId());
        Categoria catNueva = categoriaService.crearCategoria("Nueva categoria");
        String nombreNuevo = "Nuevo prod";
        BigDecimal nuevoPrecio = new BigDecimal(5000);
        productoService.cambiarCategoriaDeProducto(prod.getId(), catNueva.getId());
        productoService.cambiarPrecioDeProducto(prod.getId(), nuevoPrecio);
        productoService.cambiarNombreDeProducto(prod.getId(), nombreNuevo);

        assertEquals(nombreNuevo.trim().toLowerCase(), productoService.buscarProductoConId(prod.getId()).getNombre());
        assertEquals(nuevoPrecio, productoService.buscarProductoConId(prod.getId()).getPrecio());
        assertEquals(catNueva.getCategoria(), productoService.buscarProductoConId(prod.getId()).getCategoria().getCategoria());

    }

    @Test
    void test_noSePuedeEditarElNombreSiEstaVacioOEsNullDeUnProducto(){
        Producto prod = productoService.crearProducto(nombreProd, precioProd, cat.getId());

        Exception errorNombreNulo = assertThrows(ErrorCampoVacioONulo.class,()->{
            productoService.crearProducto(null, precioProd, cat.getId());
        });

        Exception errorNombreVacio = assertThrows(ErrorCampoVacioONulo.class, ()->{
            productoService.crearProducto("", precioProd, cat.getId());
        });

        assertEquals(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO, errorNombreNulo.getMessage());
        assertEquals(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO, errorNombreVacio.getMessage());
        assertEquals(nombreProd.trim().toLowerCase(), productoService.buscarProductoConId(prod.getId()).getNombre());

    }

    @Test 
    void test_noSePuedeEditarElPrecioSiEsNegativoONull(){
        Producto prod = productoService.crearProducto(nombreProd, precioProd, cat.getId());

        Exception errorPrecioNegativo = assertThrows(ErrorPrecioInvalido.class,()->{
            productoService.cambiarPrecioDeProducto(prod.getId(), (new BigDecimal(-1)));
        });

        Exception errorPrecioNulo = assertThrows(ErrorPrecioInvalido.class, ()->{
            productoService.cambiarPrecioDeProducto(prod.getId(), null);
        });

        assertEquals(ErrorPrecioInvalido.ERROR_PRECIO_NEGATIVO, errorPrecioNegativo.getMessage());
        assertEquals(ErrorPrecioInvalido.ERROR_PRECIO_NULO, errorPrecioNulo.getMessage());
        assertEquals(precioProd, productoService.buscarProductoConId(prod.getId()).getPrecio());

    }

    @Test
    void test_noSePuedeCambiarLaCategoriaSiLaMismoNoExiste(){
        Producto prod = productoService.crearProducto(nombreProd, precioProd, cat.getId());
        
        Exception errorCategoriaInexistente = assertThrows(ErrorCategoriaInexistente.class,()->{
            productoService.cambiarCategoriaDeProducto(prod.getId(), 99L);
        });

        assertEquals("La categoria con id: 99, no existe.", errorCategoriaInexistente.getMessage());
        assertEquals(cat.getCategoria(), productoService.buscarProductoConId(prod.getId()).getCategoria().getCategoria());
        
    }

    @Test
    void test_sePuedenObtenerTodosLosProductos(){
        productoService.crearProducto(nombreProd, precioProd, cat.getId());
        productoService.crearProducto("Otro producto", precioProd, cat.getId());
        
        List<Producto> todosLosProductos = productoService.obtenerTodosLosProductos();

        assertEquals(2, todosLosProductos.size());
    }

    @Test
    void test_sePuedenObtenerTodosLosProductosQueContienenUnaPalabraEnSuNombre(){
        productoService.crearProducto("Prueba", precioProd, cat.getId());
        productoService.crearProducto("Producto 1", precioProd, cat.getId());
        productoService.crearProducto("Producto 2", precioProd, cat.getId());
        productoService.crearProducto("Prueba 2", precioProd, cat.getId());

        List<Producto> prodConNombrePrueba = productoService.obtenerProductosConNombre("Prueba");

        assertEquals(2, prodConNombrePrueba.size());
        assertEquals(4, productoService.obtenerTodosLosProductos().size());
    }

    @Test
    void test_sePudenObtenerTodosLosProductosDeUnaCategoria(){
        Categoria cat2 = categoriaService.crearCategoria("Otra cat");
        productoService.crearProducto(nombreProd, precioProd, cat2.getId());
        productoService.crearProducto("Producto 1", precioProd, cat.getId());
        productoService.crearProducto("Producto 2", precioProd, cat.getId());
        productoService.crearProducto("Producto 3", precioProd, cat.getId());

        List<Producto> prodDeCat2 = productoService.obtenerProductosDeCategoria(cat2.getId());
        List<Producto> prodDeCat = productoService.obtenerProductosDeCategoria(cat.getId());
        
        assertEquals(1, prodDeCat2.size());
        assertEquals(3, prodDeCat.size());
    }

    @Test
    void test_sePuedeHacerUnaModificacionMasivaDeTodosLosProductosConUnaCategoria(){
        Categoria cat2 = categoriaService.crearCategoria("Otra cat");
        productoService.crearProducto(nombreProd, precioProd, cat2.getId());
        productoService.crearProducto("Producto 1", precioProd, cat2.getId());
        productoService.crearProducto("Producto 2", precioProd, cat.getId());
        productoService.crearProducto("Producto 3", precioProd, cat.getId());
        
        List<Producto> prodDeCat2 = productoService.obtenerProductosDeCategoria(cat2.getId());
        List<Producto> prodDeCat = productoService.obtenerProductosDeCategoria(cat.getId());
        
        assertEquals(2, prodDeCat2.size());
        assertEquals(2, prodDeCat.size());

        productoService.actualizarCategoriaDeProductos(cat.getId(), cat2.getId());

        List<Producto> prodDeCat2Actualizada = productoService.obtenerProductosDeCategoria(cat2.getId());

        assertEquals(4, prodDeCat2Actualizada.size());

    }

    @Test
    void test_noSePuedeCambiarLaCategoriaDeTodosLosProductosDeUnaCategoriaSiLaCategoriaActualONuevoNoExiste(){
        productoService.crearProducto("Prueba", precioProd, cat.getId());
        productoService.crearProducto("Producto 1", precioProd, cat.getId());
        productoService.crearProducto("Producto 2", precioProd, cat.getId());
        productoService.crearProducto("Prueba 2", precioProd, cat.getId());

        Exception errorCategoriaInexistente = assertThrows(ErrorCategoriaInexistente.class, ()->{
            productoService.actualizarCategoriaDeProductos(99L, 108L);
        });

        Exception errorCategoriaNuevaInexistente = assertThrows(ErrorCategoriaInexistente.class, ()->{
            productoService.actualizarCategoriaDeProductos(cat.getId(), 108L);
        });

        assertEquals("La categoria con id: 99, no existe.",errorCategoriaInexistente.getMessage());
        assertEquals("La categoria con id: 108, no existe.", errorCategoriaNuevaInexistente.getMessage());
            
    }
}
