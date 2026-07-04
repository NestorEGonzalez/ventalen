package com.ventalen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import com.ventalen.exception.ErrorProveedorInexistente;
import com.ventalen.exception.ErrorUsuarioNoValido;
import com.ventalen.ingreso.DetalleIngresoRepository;
import com.ventalen.ingreso.DetalleIngresoRequest;
import com.ventalen.ingreso.Ingreso;
import com.ventalen.ingreso.IngresoRepository;
import com.ventalen.ingreso.IngresoService;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.proveedor.Proveedor;
import com.ventalen.proveedor.ProveedorRepository;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;

@ExtendWith(MockitoExtension.class)
class IngresoServiceTest {

    @InjectMocks
    private IngresoService ingresoService;

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

    private Usuario usuario;
    private Proveedor proveedor;
    private Producto producto;
    private Stock stock;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("admin", "pass", com.ventalen.auth.Rol.ADMIN);
        proveedor = new Proveedor("Proveedor 1", "Corredor", "123", true);
        producto = new Producto("Producto", new BigDecimal("10.00"), new com.ventalen.categoria.Categoria("Cat"));
        ReflectionTestUtils.setField(producto, "id", 10L);
        stock = new Stock(producto, 5);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pass"));
    }

    @Test
    void test_crearIngresoGuardaYActualizaStock() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(productoService.buscarProductoConId(10L)).thenReturn(producto);
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.of(stock));
        when(ingresoRepository.save(any(Ingreso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DetalleIngresoRequest detalle = new DetalleIngresoRequest(10L, 3, new BigDecimal("8.50"));
        Ingreso ingreso = ingresoService.crear(1L, List.of(detalle));

        assertNotNull(ingreso);
        assertEquals(1, ingreso.getDetalles().size());
        assertEquals(8, stock.getCantidad());
        verify(stockRepository).save(stock);
    }

    @Test
    void test_crearIngresoConProveedorInexistenteLanzaExcepcion() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        ErrorProveedorInexistente exception = assertThrows(ErrorProveedorInexistente.class,
                () -> ingresoService.crear(99L, List.of()));

        assertEquals("El proveedor con id 99, no existe.", exception.getMessage());
    }

    @Test
    void test_obtenerTodosRetornaLista() {
        when(ingresoRepository.findAll()).thenReturn(List.of(new Ingreso(usuario, proveedor)));

        List<Ingreso> ingresos = ingresoService.obtenerTodos();

        assertEquals(1, ingresos.size());
    }

    @Test
    void test_buscarPorIdInexistenteLanzaExcepcion() {
        when(ingresoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(com.ventalen.exception.ErrorIngresoInexistente.class,
                () -> ingresoService.buscarPorId(99L));
    }

    @Test
    void test_crearIngresoSinUsuarioValidoLanzaExcepcion() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.empty());

        ErrorUsuarioNoValido exception = assertThrows(ErrorUsuarioNoValido.class,
                () -> ingresoService.crear(1L, List.of()));

        assertEquals(ErrorUsuarioNoValido.ERROR_USUARIO_INEXISTENTE, exception.getMessage());
    }
}
