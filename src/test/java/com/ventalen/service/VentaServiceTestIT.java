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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ventalen.TestBase;
import com.ventalen.auth.Usuario;
import com.ventalen.auth.UsuarioRepository;
import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaService;
import com.ventalen.exception.ErrorStockInsuficiente;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;
import com.ventalen.venta.DetalleVentaRequest;
import com.ventalen.venta.Venta;
import com.ventalen.venta.VentaService;

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
    private UsuarioRepository usuarioRepository;

    private Producto producto;

    @BeforeEach
    void setUp() {
        Usuario usuario = usuarioRepository.findByUsername("admin-test").orElseGet(() -> usuarioRepository.save(new Usuario("admin-test", "test4321", com.ventalen.auth.Rol.ADMIN)));
        Categoria categoria = categoriaService.crearCategoria("Categoría venta");
        producto = productoService.crearProducto("Producto venta", new BigDecimal("20.00"), categoria.getId());
        stockRepository.save(new Stock(producto, 7));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin-test", "test4321"));
    }

    @Test
    void test_crearVentaYActualizarStock() {
        Venta venta = ventaService.crear(true, List.of(new DetalleVentaRequest(producto.getId(), 3, new BigDecimal("22.00"))));

        assertNotNull(venta);
        assertEquals(1, venta.getDetalles().size());
        assertEquals(4, stockRepository.findByProducto(producto).orElseThrow().getCantidad());
    }

    @Test
    void test_crearVentaConStockInsuficienteLanzaExcepcion() {
        ErrorStockInsuficiente exception = assertThrows(ErrorStockInsuficiente.class,
                () -> ventaService.crear(false, List.of(new DetalleVentaRequest(producto.getId(), 10, new BigDecimal("22.00")))));

        assertEquals("Stock insuficiente para el producto con id " + producto.getId() + ". Disponible: 7, solicitado: 10.", exception.getMessage());
    }
}
