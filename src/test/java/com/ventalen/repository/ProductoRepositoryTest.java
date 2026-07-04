package com.ventalen.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
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
import com.ventalen.exception.ErrorPrecioInvalido;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoRepository;

import jakarta.transaction.Transactional;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class ProductoRepositoryTest extends TestBase {
    
    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    ProductoRepository productoRepository;

    String nombre;
    BigDecimal precio;
    Categoria categoria;
    Producto prod;

    @BeforeEach
    void sepUp(){
        nombre = "Producto";
        precio = new BigDecimal("70");
        categoria = categoriaRepository.save(new Categoria("Prueba"));
        prod = new Producto(nombre, precio, categoria);

    }

    @Test
    void test01_sePuedeCrearYGuardarUnProducto(){
        Producto producto = productoRepository.save(prod);

        assertNotNull(producto);

        assertEquals(1,productoRepository.count());

    }

    @Test
    void test02_sePuedeBuscarUnProductoPorSuNombreYObtenerSuId(){
        Producto producto = productoRepository.save(prod);

        assertEquals(producto.getId(), productoRepository.findOneByNombreIgnoreCase(nombre).get().getId());

    }
    
    @Test
    void test03_sePuedenObtenerTodosLosProductos(){
        Producto prod2 = new Producto("Prod2", (new BigDecimal("40")),categoria);
        
        productoRepository.save(prod);
        productoRepository.save(prod2);
        
        List<Producto> todosLosProductos = productoRepository.findAll();
        
        assertEquals(2, todosLosProductos.size());

    }

    @Test
    void test04_sePuedeEliminarUnProducto(){
        Producto prod2 = new Producto("Prod2", (new BigDecimal("40")),categoria);
        
        Producto producto1 = productoRepository.save(prod);
        productoRepository.save(prod2);

        assertEquals(2, productoRepository.count());

        productoRepository.deleteById(producto1.getId());

        assertEquals(1, productoRepository.count());

    }

    @Test
    void test05_sePuedeModificarUnProducto(){
        Producto productoGuardado = productoRepository.save(prod); 
        Producto prodModificado = productoRepository.findOneByNombreIgnoreCase(nombre).get();
        
        assertEquals(productoGuardado.getId(), prodModificado.getId());
        assertEquals(productoGuardado.getNombre(), prodModificado.getNombre());
        assertEquals(productoGuardado.getPrecio(), prodModificado.getPrecio());
        assertEquals(productoGuardado.getCategoria(), prodModificado.getCategoria());
        
        String nuevoNombre = "Nuevo nombre";
        BigDecimal nuevoPrecio = new BigDecimal("500");
        Categoria nuevaCategoria = categoriaRepository.save(new Categoria("NuevaCategoria"));
        prodModificado.setPrecio(nuevoPrecio);
        prodModificado.setNombre(nuevoNombre);
        prodModificado.setCategoria(nuevaCategoria);
        
        productoRepository.save(prodModificado);

        assertEquals(nuevoNombre.trim().toLowerCase(), productoRepository.findById(productoGuardado.getId()).get().getNombre());
        assertEquals(nuevoPrecio, productoRepository.findById(productoGuardado.getId()).get().getPrecio());
        assertEquals(nuevaCategoria.getCategoria(), productoRepository.findById(productoGuardado.getId()).get().getCategoria().getCategoria());

    }

    @Test
    void test06_sePuedeModificarLaCategoriaDeTodosLosProductosDeUnaCategoria(){
        productoRepository.save(prod);
        productoRepository.save(new Producto("Otro",(new BigDecimal(50)),categoria));
        Categoria otraCategoria = categoriaRepository.save(new Categoria("OtraCategoria"));
        
        productoRepository.save(new Producto("OtroProducto",(new BigDecimal(50)),otraCategoria));

        assertEquals(2, productoRepository.findByCategoria(categoria).size());

        Categoria nuevaCategoria = categoriaRepository.save(new Categoria("Nueva"));
        
        productoRepository.actualizarCategoria(nuevaCategoria, categoria);

        assertEquals(0, productoRepository.findByCategoria(categoria).size());
        assertEquals(2, productoRepository.findByCategoria(nuevaCategoria).size());
        assertEquals(1, productoRepository.findByCategoria(otraCategoria).size());

    }

    @Test
    void test07_noSePuedenGuardarProductosConNombreNuloOVacio(){
        Exception errorNombreNulo = assertThrows(ErrorCampoVacioONulo.class, ()->{
            productoRepository.save(new Producto(null, precio,categoria));
        });

        Exception errorNombreVacio = assertThrows(ErrorCampoVacioONulo.class, ()->{
            productoRepository.save(new Producto("", precio,categoria));
        });

        assertEquals(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO,errorNombreNulo.getMessage());
        assertEquals(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO, errorNombreVacio.getMessage());
    }

    @Test
    void test08_noSePuedenGuardarProductosConPrecioNegativo(){
        BigDecimal precioNegativo = new BigDecimal("-1");
        Exception errorPrecioNegativo = assertThrows(ErrorPrecioInvalido.class, ()->{
            productoRepository.save(new Producto(nombre, precioNegativo, categoria));
        });

        Exception precioNulo = assertThrows(ErrorPrecioInvalido.class, ()->{
            productoRepository.save(new Producto(nombre,null, categoria));
        });

        assertEquals(ErrorPrecioInvalido.ERROR_PRECIO_NEGATIVO,errorPrecioNegativo.getMessage());
        assertEquals(ErrorPrecioInvalido.ERROR_PRECIO_NULO, precioNulo.getMessage());
    }

    @Test
    void test09_noSePuedenGuardarProductosConElMismoNombre(){
        productoRepository.save(prod);

        assertThrows(DataIntegrityViolationException.class, ()->{
            productoRepository.save(new Producto(nombre, precio, categoria));
        });
    }

    @Test
    void test10_noSePuedenGuardarProductosSinCategoriaOConUnaCategoriaInexistente(){
        Producto productoConCatNula = new Producto(nombre, precio, null);

        assertThrows(DataIntegrityViolationException.class, ()->{
            productoRepository.save(productoConCatNula);
        });
    }

    @Test
    void test11_sePuedenTraerTodosLosProductosQueContengaUnaPalabraEnSuNombre(){
        Producto prod1 = new Producto("Producto uno", precio, categoria);
        Producto prod2 = new Producto("Producto dos", precio, categoria);
        Producto prod3 = new Producto("Tres", precio, categoria);

        productoRepository.save(prod1);
        productoRepository.save(prod2);
        productoRepository.save(prod3);

        assertEquals(3, productoRepository.count());
        assertEquals(2, productoRepository.buscarPorNombreConCategoria(nombre).size());

    }
}
