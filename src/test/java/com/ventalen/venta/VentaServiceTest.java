package com.ventalen.venta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ventalen.auth.Usuario;
import com.ventalen.auth.UsuarioRepository;
import com.ventalen.detalleVenta.DetalleVentaRepository;
import com.ventalen.detalleVenta.DetalleVentaRequest;
import com.ventalen.exception.ErrorStockInsuficiente;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

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

    @InjectMocks
    private VentaService ventaService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void noSePuedeVenderMasQueElStockDisponible() {
        Usuario usuario = new Usuario();
        Producto producto = new Producto();
        Stock stockInicial = new Stock(producto, 2);
        DetalleVentaRequest detalle = new DetalleVentaRequest(11L, 5, new BigDecimal("20"));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(productoService.buscarProductoConId(11L)).thenReturn(producto);
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.of(stockInicial));

        assertThrows(ErrorStockInsuficiente.class, () -> ventaService.crear(false, List.of(detalle)));

        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void crearVentaRestaStock() {
        Usuario usuario = new Usuario();
        Producto producto = new Producto();
        Stock stockInicial = new Stock(producto, 10);
        DetalleVentaRequest detalle = new DetalleVentaRequest(12L, 4, new BigDecimal("25"));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(productoService.buscarProductoConId(12L)).thenReturn(producto);
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.of(stockInicial));
        when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venta venta = ventaService.crear(true, List.of(detalle));

        assertEquals(6, stockInicial.getCantidad());
        verify(stockRepository).save(stockInicial);
        assertEquals(Boolean.TRUE, venta.getPagado());
    }
}
