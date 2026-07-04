package com.ventalen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ventalen.TestBase;
import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaService;
import com.ventalen.exception.ErrorStockConIdInexistente;
import com.ventalen.exception.ErrorStockYaExistente;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockService;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class StockServiceTestIT extends TestBase {

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        Categoria categoria = categoriaService.crearCategoria("Categoría stock");
        producto = productoService.crearProducto("Producto stock", new BigDecimal("30.00"), categoria.getId());
    }

    @Test
    void test_crearYBuscarStock() {
        Stock stock = stockService.crearStock(producto.getId(), 4);

        assertNotNull(stock);
        assertEquals(4, stock.getCantidad());
        assertEquals(producto.getId(), stock.getProducto().getId());
    }

    @Test
    void test_noSePuedeCrearStockDuplicado() {
        stockService.crearStock(producto.getId(), 4);

        ErrorStockYaExistente exception = assertThrows(ErrorStockYaExistente.class,
                () -> stockService.crearStock(producto.getId(), 5));

        assertEquals("Ya existe stock para el producto con id " + producto.getId() + ".", exception.getMessage());
    }

    @Test
    void test_buscarStockInexistenteLanzaExcepcion() {
        ErrorStockConIdInexistente exception = assertThrows(ErrorStockConIdInexistente.class,
                () -> stockService.buscarStockConId(999L));

        assertEquals("El stock con id 999, no existe.", exception.getMessage());
    }
}
