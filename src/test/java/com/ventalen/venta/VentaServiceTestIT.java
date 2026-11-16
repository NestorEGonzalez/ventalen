package com.ventalen.venta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ventalen.TestBase;
import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaService;
import com.ventalen.cliente.Cliente;
import com.ventalen.cliente.ClienteService;
import com.ventalen.detalleVenta.DetalleVentaRequest;
import com.ventalen.exception.ErrorClienteInactivo;
import com.ventalen.exception.ErrorStockInsuficiente;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class VentaServiceTestIT extends TestBase {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ClienteService clienteService;

    private Producto producto;
    private Stock stock;

    @BeforeEach
    void setUp() {
        org.springframework.security.core.context.SecurityContextHolder.getContext()
                .setAuthentication(new org.springframework.security.authentication
                        .UsernamePasswordAuthenticationToken("admin-test", ""));

        Categoria cat = categoriaService.crearCategoria("Categoria Test");
        producto = productoService.crearProducto("Producto Test", new BigDecimal("10.00"), cat.getId());
        stock = stockRepository.save(new Stock(producto, 10));
    }

    @Test
    void test01_crearVentaRestaStock() {
        DetalleVentaRequest detalle = new DetalleVentaRequest(producto.getId(), 4, new BigDecimal("15"));

        Venta venta = ventaService.crear(true, null, java.util.List.of(detalle));

        assertNotNull(venta);
        assertEquals(6, stockRepository.findByProducto(producto).get().getCantidad());
    }

    @Test
    void test02_noSePuedeVenderMasQueElStockDisponible() {
        DetalleVentaRequest detalle = new DetalleVentaRequest(producto.getId(), 15, new BigDecimal("15"));

        assertThrows(ErrorStockInsuficiente.class,
                () -> ventaService.crear(false, null, java.util.List.of(detalle)));
    }

    @Test
    void test03_noSePuedeVenderSinStockParaElProducto() {
        Categoria cat2 = categoriaService.crearCategoria("Sin Stock");
        Producto prodSinStock = productoService.crearProducto("Prod Sin Stock", new BigDecimal("5.00"), cat2.getId());

        DetalleVentaRequest detalle = new DetalleVentaRequest(prodSinStock.getId(), 1, new BigDecimal("5"));

        assertThrows(ErrorStockInsuficiente.class,
                () -> ventaService.crear(false, null, java.util.List.of(detalle)));
    }

    @Test
    void test04_noSePuedeVenderAUnClienteInactivo() {
        Cliente cliente = clienteService.crear("Cliente Test", "12345678");
        clienteService.eliminar(cliente.getId());

        DetalleVentaRequest detalle = new DetalleVentaRequest(producto.getId(), 2, new BigDecimal("15"));

        assertThrows(ErrorClienteInactivo.class,
                () -> ventaService.crear(false, cliente.getId(), java.util.List.of(detalle)));
    }

    @Test
    void test05_sePuedeCrearVentaConClienteActivo() {
        Cliente cliente = clienteService.crear("Cliente Activo", "12345678");

        DetalleVentaRequest detalle = new DetalleVentaRequest(producto.getId(), 2, new BigDecimal("15"));

        Venta venta = ventaService.crear(true, cliente.getId(), java.util.List.of(detalle));

        assertNotNull(venta.getCliente());
        assertEquals(cliente.getId(), venta.getCliente().getId());
    }
    @Test
    void test06_sePuedeCrearUnaVentaYLuegoPagarla(){
        Cliente cliente = clienteService.crear("Cliente", "1122334455");
        DetalleVentaRequest detalle = new DetalleVentaRequest(producto.getId(), 2, new BigDecimal("15"));
        Venta venta = ventaService.crear(false, cliente.getId(), java.util.List.of(detalle));

        assertFalse(venta.getPagado());

        ventaService.pagar(venta.getId());

        assertTrue(venta.getPagado());
    }
}
