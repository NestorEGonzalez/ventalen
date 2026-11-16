package com.ventalen.venta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ventalen.auth.Usuario;
import com.ventalen.auth.UsuarioRepository;
import com.ventalen.cliente.Cliente;
import com.ventalen.cliente.ClienteRepository;
import com.ventalen.detalleVenta.DetalleVentaRepository;
import com.ventalen.detalleVenta.DetalleVentaRequest;
import com.ventalen.exception.ErrorClienteInactivo;
import com.ventalen.exception.ErrorClienteInexistente;
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

    @Mock
    private ClienteRepository clienteRepository;

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
    void test01_noSePuedeVenderMasQueElStockDisponible() {
        Usuario usuario = new Usuario();
        Producto producto = new Producto();
        Stock stockInicial = new Stock(producto, 2);
        DetalleVentaRequest detalle = new DetalleVentaRequest(11L, 5, new BigDecimal("20"));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(productoService.buscarProductoConId(11L)).thenReturn(producto);
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.of(stockInicial));

        assertThrows(ErrorStockInsuficiente.class, () -> ventaService.crear(false, null, List.of(detalle)));

        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void test02_crearVentaRestaStock() {
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

        Venta venta = ventaService.crear(true, null, List.of(detalle));

        assertEquals(6, stockInicial.getCantidad());
        verify(stockRepository).save(stockInicial);
        assertEquals(Boolean.TRUE, venta.getPagado());
    }

    @Test
    void test03_noSePuedeVenderSinStockParaElProducto() {
        Usuario usuario = new Usuario();
        Producto producto = new Producto();
        DetalleVentaRequest detalle = new DetalleVentaRequest(13L, 3, new BigDecimal("15"));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(productoService.buscarProductoConId(13L)).thenReturn(producto);
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.empty());

        assertThrows(ErrorStockInsuficiente.class, () -> ventaService.crear(false, null, List.of(detalle)));

        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void test04_noSePuedeVenderAUnClienteInactivo() {
        Usuario usuario = new Usuario();
        DetalleVentaRequest detalle = new DetalleVentaRequest(14L, 2, new BigDecimal("20"));
        Cliente cliente = new Cliente("Cliente Test", "12345678");
        cliente.desactivarCliente();

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        assertThrows(ErrorClienteInactivo.class, () -> ventaService.crear(false, 1L, List.of(detalle)));
    }

    @Test
    void test05_sePuedeCrearUnaVentaConClienteActivo() {
        Usuario usuario = new Usuario();
        Producto producto = new Producto();
        Stock stockInicial = new Stock(producto, 10);
        DetalleVentaRequest detalle = new DetalleVentaRequest(15L, 2, new BigDecimal("25"));
        Cliente cliente = new Cliente("Cliente Activo", "12345678");

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoService.buscarProductoConId(15L)).thenReturn(producto);
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.of(stockInicial));
        when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venta venta = ventaService.crear(true, 1L, List.of(detalle));

        assertNotNull(venta.getCliente());
        assertEquals(8, stockInicial.getCantidad());
        verify(ventaRepository).save(any(Venta.class));
    }

    @Test
    void test06_sePuedeCrearUnaVentaSinCliente() {
        Usuario usuario = new Usuario();
        Producto producto = new Producto();
        Stock stockInicial = new Stock(producto, 10);
        DetalleVentaRequest detalle = new DetalleVentaRequest(16L, 2, new BigDecimal("25"));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(productoService.buscarProductoConId(16L)).thenReturn(producto);
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.of(stockInicial));
        when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venta venta = ventaService.crear(true, null, List.of(detalle));

        assertEquals(null, venta.getCliente());
        assertEquals(8, stockInicial.getCantidad());
    }

  }
