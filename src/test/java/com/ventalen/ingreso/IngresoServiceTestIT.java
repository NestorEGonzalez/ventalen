package com.ventalen.ingreso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ventalen.TestBase;
import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaService;
import com.ventalen.detalleIngreso.DetalleIngresoRequest;
import com.ventalen.exception.ErrorProductoConIdInexistente;
import com.ventalen.exception.ErrorProductoInactivo;
import com.ventalen.exception.ErrorProveedorInexistente;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.proveedor.Proveedor;
import com.ventalen.proveedor.ProveedorService;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class IngresoServiceTestIT extends TestBase {

    @Autowired
    private IngresoService ingresoService;

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private StockRepository stockRepository;

    private Proveedor proveedor;
    private Categoria categoria;
    private Producto producto;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin-test", ""));
        proveedor = proveedorService.crear("Test Prov", "Corredor", "123456");
        categoria = categoriaService.crearCategoria("Test Cat");
        producto = productoService.crearProducto("Test Prod", new BigDecimal("10.00"), categoria.getId());
    }

    @Test
    void test01_crearIngresoConProductoSinStock() {
        Ingreso ingreso = ingresoService.crear(proveedor.getId(),
                List.of(new DetalleIngresoRequest(producto.getId(), 3, new BigDecimal("15.50"))));

        assertNotNull(ingreso);
        Optional<Stock> stock = stockRepository.findByProducto(producto);
        assertTrue(stock.isPresent());
        assertEquals(3, stock.get().getCantidad());
    }

    @Test
    void test02_noSePuedeIngresarCantidadNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> ingresoService.crear(proveedor.getId(),
                        List.of(new DetalleIngresoRequest(producto.getId(), -1, new BigDecimal("15.50")))));
    }

    @Test
    void test03_noSePuedeIngresarProductoInexistente() {
        assertThrows(ErrorProductoConIdInexistente.class,
                () -> ingresoService.crear(proveedor.getId(),
                        List.of(new DetalleIngresoRequest(999L, 3, new BigDecimal("15.50")))));
    }

    @Test
    void test04_noSePuedeIngresarProductoInactivo() {
        producto.desactivarProducto();

        assertThrows(ErrorProductoInactivo.class,
                () -> ingresoService.crear(proveedor.getId(),
                        List.of(new DetalleIngresoRequest(producto.getId(), 3, new BigDecimal("15.50")))));
    }

    @Test
    void test05_sePuedeCrearUnIngresoConProductoConStockExistente() {
        stockRepository.save(new Stock(producto, 5));

        Ingreso ingreso = ingresoService.crear(proveedor.getId(),
                List.of(new DetalleIngresoRequest(producto.getId(), 3, new BigDecimal("15.50"))));

        assertNotNull(ingreso);
        Optional<Stock> stock = stockRepository.findByProducto(producto);
        assertTrue(stock.isPresent());
        assertEquals(8, stock.get().getCantidad());
    }
}
