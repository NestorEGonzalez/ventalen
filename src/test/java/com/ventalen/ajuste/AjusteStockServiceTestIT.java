package com.ventalen.ajuste;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ventalen.TestBase;
import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaService;
import com.ventalen.exception.ErrorMotivoInexistente;
import com.ventalen.exception.ErrorProductoInactivo;
import com.ventalen.exception.ErrorStockInsuficiente;
import com.ventalen.motivo.Motivo;
import com.ventalen.motivo.MotivoRepository;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;
import com.ventalen.stock.StockService;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class AjusteStockServiceTestIT extends TestBase {

    @Autowired
    private AjusteStockService ajusteStockService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private MotivoRepository motivoRepository;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin-test", ""));
    }

    @Test
    void test01_ajustePositivoSumaStock() {
        Categoria categoria = categoriaService.crearCategoria("Cat Test 1");
        Producto producto = productoService.crearProducto("Prod Test 1", new BigDecimal("10.00"), categoria.getId());
        stockService.crearStock(producto.getId(), 4);
        Motivo motivo = motivoRepository.save(new Motivo("Ingreso extra", true));

        AjusteStock ajuste = ajusteStockService.crear(producto.getId(), 2, motivo.getId(), "sumar");

        Stock stock = stockRepository.findByProducto(producto).orElseThrow();
        assertEquals(6, stock.getCantidad());
        assertEquals("Ingreso extra", ajuste.getMotivo().getMotivo());
    }

    @Test
    void test02_noSePuedeDescontarStockSiNoHaySuficiente() {
        Categoria categoria = categoriaService.crearCategoria("Cat Test 2");
        Producto producto = productoService.crearProducto("Prod Test 2", new BigDecimal("15.00"), categoria.getId());
        stockService.crearStock(producto.getId(), 3);
        Motivo motivo = motivoRepository.save(new Motivo("Pérdida", false));

        assertThrows(ErrorStockInsuficiente.class,
                () -> ajusteStockService.crear(producto.getId(), 5, motivo.getId(), "sin stock"));
    }

    @Test
    void test03_ajustePositivoEnStockInexistenteCreaStock() {
        Categoria categoria = categoriaService.crearCategoria("Cat Test 3");
        Producto producto = productoService.crearProducto("Prod Test 3", new BigDecimal("20.00"), categoria.getId());
        Motivo motivo = motivoRepository.save(new Motivo("Ingreso inicial", true));

        AjusteStock ajuste = ajusteStockService.crear(producto.getId(), 10, motivo.getId(), "nuevo stock");

        Stock stock = stockRepository.findByProducto(producto).orElseThrow();
        assertEquals(10, stock.getCantidad());
        assertEquals(producto.getId(), ajuste.getProducto().getId());
    }

    @Test
    void test04_noSePuedeAjustarProductoInactivo() {
        Categoria categoria = categoriaService.crearCategoria("Cat Test 4");
        Producto producto = productoService.crearProducto("Prod Test 4", new BigDecimal("25.00"), categoria.getId());
        stockService.crearStock(producto.getId(), 10);
        Motivo motivo = motivoRepository.save(new Motivo("Ingreso extra", true));

        productoService.borrarProductoConId(producto.getId());

        assertThrows(ErrorProductoInactivo.class,
                () -> ajusteStockService.crear(producto.getId(), 5, motivo.getId(), "inactivo"));
    }
}
