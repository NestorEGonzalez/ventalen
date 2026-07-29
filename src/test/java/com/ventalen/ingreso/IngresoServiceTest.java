package com.ventalen.ingreso;

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
import com.ventalen.detalleIngreso.DetalleIngresoRepository;
import com.ventalen.detalleIngreso.DetalleIngresoRequest;
import com.ventalen.exception.ErrorProductoConIdInexistente;
import com.ventalen.exception.ErrorProductoInactivo;
import com.ventalen.exception.ErrorProveedorInexistente;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.proveedor.Proveedor;
import com.ventalen.proveedor.ProveedorRepository;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;

@ExtendWith(MockitoExtension.class)
class IngresoServiceTest {

    @Mock
    private IngresoRepository ingresoRepository;

    @Mock
    private DetalleIngresoRepository detalleIngresoRepository;

    @Mock
    private ProductoService productoService;

    @Mock
    private ProveedorRepository proveedorRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private IngresoService ingresoService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void test01_crearIngresoSumaStockAlProductoExistente() {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        Proveedor proveedor = new Proveedor("Proveedor Test", "Corredor", "123456");
        Producto producto = new Producto();
        Stock stockInicial = new Stock(producto, 5);
        DetalleIngresoRequest detalle = new DetalleIngresoRequest(10L, 3, new BigDecimal("15.50"));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(proveedorRepository.findById(7L)).thenReturn(Optional.of(proveedor));
        when(productoService.buscarProductoConId(10L)).thenReturn(producto);
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.of(stockInicial));
        when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ingresoRepository.save(any(Ingreso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ingreso ingreso = ingresoService.crear(7L, List.of(detalle));

        assertNotNull(ingreso);
        assertEquals(8, stockInicial.getCantidad());
        verify(stockRepository).save(stockInicial);
    }

    @Test
    void test02_noSePuedeIngresarCantidadNegativa() {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        Proveedor proveedor = new Proveedor("Proveedor Test", "Corredor", "123456");
        Producto producto = new Producto();
        Stock stockInicial = new Stock(producto, 5);
        DetalleIngresoRequest detalle = new DetalleIngresoRequest(10L, -1, new BigDecimal("15.50"));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(proveedorRepository.findById(7L)).thenReturn(Optional.of(proveedor));
        when(productoService.buscarProductoConId(10L)).thenReturn(producto);
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.of(stockInicial));

        assertThrows(IllegalArgumentException.class,
                () -> ingresoService.crear(7L, List.of(detalle)));
    }

    @Test
    void test03_noSePuedeIngresarProductoInexistente() {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        Proveedor proveedor = new Proveedor("Proveedor Test", "Corredor", "123456");
        DetalleIngresoRequest detalle = new DetalleIngresoRequest(99L, 3, new BigDecimal("15.50"));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(proveedorRepository.findById(7L)).thenReturn(Optional.of(proveedor));
        when(productoService.buscarProductoConId(99L)).thenThrow(new ErrorProductoConIdInexistente(99L));

        assertThrows(ErrorProductoConIdInexistente.class,
                () -> ingresoService.crear(7L, List.of(detalle)));

        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void test04_noSePuedeIngresarProductoInactivo() {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        Proveedor proveedor = new Proveedor("Proveedor Test", "Corredor", "123456");
        Producto producto = new Producto();
        producto.desactivarProducto();
        DetalleIngresoRequest detalle = new DetalleIngresoRequest(10L, 3, new BigDecimal("15.50"));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(proveedorRepository.findById(7L)).thenReturn(Optional.of(proveedor));
        when(productoService.buscarProductoConId(10L)).thenReturn(producto);

        assertThrows(ErrorProductoInactivo.class,
                () -> ingresoService.crear(7L, List.of(detalle)));

        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void test05_alIngresarProductoSinStockSeCreaStockEnCeroYSeSuma() {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        Proveedor proveedor = new Proveedor("Proveedor Test", "Corredor", "123456");
        Producto producto = new Producto();
        DetalleIngresoRequest detalle = new DetalleIngresoRequest(10L, 5, new BigDecimal("15.50"));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(proveedorRepository.findById(7L)).thenReturn(Optional.of(proveedor));
        when(productoService.buscarProductoConId(10L)).thenReturn(producto);
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.empty());
        when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ingresoRepository.save(any(Ingreso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ingreso ingreso = ingresoService.crear(7L, List.of(detalle));

        assertNotNull(ingreso);
        verify(stockRepository, times(2)).save(any(Stock.class));
    }

    @Test
    void test06_noSePuedeIngresarProveedorInexistente() {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        DetalleIngresoRequest detalle = new DetalleIngresoRequest(10L, 3, new BigDecimal("15.50"));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ErrorProveedorInexistente.class,
                () -> ingresoService.crear(99L, List.of(detalle)));

        verify(stockRepository, never()).save(any(Stock.class));
    }
}
