package com.ventalen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ventalen.categoria.Categoria;
import com.ventalen.exception.ErrorCantidadInvalida;
//import com.ventalen.exception.ErrorStockConIdInexistente;
import com.ventalen.exception.ErrorStockYaExistente;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;
import com.ventalen.stock.StockService;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductoService productoService;

    private Producto producto;
    private Stock stock;

    @BeforeEach
    void setUp() {
        producto = new Producto("Producto", new java.math.BigDecimal("10.00"), new Categoria("Cat"));
        ReflectionTestUtils.setField(producto, "id", 10L);
        stock = new Stock(producto, 3);
        ReflectionTestUtils.setField(stock, "id", 1L);
    }

    @Test
    void test_crearStockGuardaYDevuelveEntidad() {
        when(productoService.buscarProductoConId(10L)).thenReturn(producto);
        when(stockRepository.existsByProducto(producto)).thenReturn(false);
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        Stock creado = stockService.crearStock(10L, 3);

        assertNotNull(creado);
        assertEquals(3, creado.getCantidad());
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    void test_crearStockConCantidadInvalidaLanzaExcepcion() {
        ErrorCantidadInvalida exception = assertThrows(ErrorCantidadInvalida.class,
                () -> stockService.crearStock(10L, -1));

        assertEquals(ErrorCantidadInvalida.ERROR_CANTIDAD_NEGATIVA, exception.getMessage());
    }

    @Test
    void test_crearStockConProductoYaExistenteLanzaExcepcion() {
        when(productoService.buscarProductoConId(10L)).thenReturn(producto);
        when(stockRepository.existsByProducto(producto)).thenReturn(true);

        ErrorStockYaExistente exception = assertThrows(ErrorStockYaExistente.class,
                () -> stockService.crearStock(10L, 2));

        assertEquals("Ya existe stock para el producto con id 10.", exception.getMessage());
    }

    @Test
    void test_buscarYObtenerStocks() {
        when(stockRepository.findAll()).thenReturn(List.of(stock));
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(productoService.buscarProductoConId(10L)).thenReturn(producto);
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.of(stock));

        assertEquals(1, stockService.obtenerTodosLosStocks().size());
        assertEquals(stock, stockService.buscarStockConId(1L));
        assertEquals(stock, stockService.buscarStockPorProducto(10L));
    }

    @Test
    void test_borrarYModificarStock() {
        when(stockRepository.existsById(1L)).thenReturn(true);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        stockService.borrarStockConId(1L);
        Stock modificado = stockService.cambiarCantidad(1L, 5);

        assertEquals(5, modificado.getCantidad());
        verify(stockRepository).deleteById(1L);
    }
}
