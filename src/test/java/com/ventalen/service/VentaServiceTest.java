package com.ventalen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.ventalen.auth.Usuario;
import com.ventalen.auth.UsuarioRepository;
import com.ventalen.exception.ErrorStockInsuficiente;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;
import com.ventalen.venta.DetalleVentaRepository;
import com.ventalen.venta.DetalleVentaRequest;
import com.ventalen.venta.Venta;
import com.ventalen.venta.VentaRepository;
import com.ventalen.venta.VentaService;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @InjectMocks
    private VentaService ventaService;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private ProductoService productoService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;
    private Producto producto;
    private Stock stock;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("admin", "pass", com.ventalen.auth.Rol.ADMIN);
        producto = new Producto("Producto", new BigDecimal("10.00"), new com.ventalen.categoria.Categoria("Cat"));
        ReflectionTestUtils.setField(producto, "id", 10L);
        stock = new Stock(producto, 5);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pass"));
    }

    @Test
    void test_crearVentaConStockSuficiente() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(productoService.buscarProductoConId(10L)).thenReturn(producto);
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.of(stock));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venta venta = ventaService.crear(true, List.of(new DetalleVentaRequest(10L, 3, new BigDecimal("11.00"))));

        assertNotNull(venta);
        assertEquals(1, venta.getDetalles().size());
        assertEquals(2, stock.getCantidad());
        verify(stockRepository).save(stock);
    }

    @Test
    void test_crearVentaConStockInsuficienteLanzaExcepcion() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(productoService.buscarProductoConId(10L)).thenReturn(producto);
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.of(stock));

        ErrorStockInsuficiente exception = assertThrows(ErrorStockInsuficiente.class,
                () -> ventaService.crear(false, List.of(new DetalleVentaRequest(10L, 10, new BigDecimal("11.00")))));

        assertEquals("Stock insuficiente para el producto con id 10. Disponible: 5, solicitado: 10.", exception.getMessage());
    }

    @Test
    void test_obtenerTodasYBuscarPorId() {
        when(ventaRepository.findAll()).thenReturn(List.of(new Venta(usuario, true)));
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(new Venta(usuario, true)));

        assertEquals(1, ventaService.obtenerTodas().size());
        assertNotNull(ventaService.buscarPorId(1L));
    }
}
