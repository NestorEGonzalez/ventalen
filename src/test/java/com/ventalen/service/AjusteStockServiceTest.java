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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ventalen.ajuste.AjusteStock;
import com.ventalen.ajuste.AjusteStockRepository;
import com.ventalen.ajuste.AjusteStockService;
import com.ventalen.auth.Usuario;
import com.ventalen.auth.UsuarioRepository;
import com.ventalen.exception.ErrorMotivoInexistente;
import com.ventalen.motivo.Motivo;
import com.ventalen.motivo.MotivoRepository;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;

@ExtendWith(MockitoExtension.class)
class AjusteStockServiceTest {

    @InjectMocks
    private AjusteStockService ajusteStockService;

    @Mock
    private AjusteStockRepository ajusteStockRepository;

    @Mock
    private ProductoService productoService;

    @Mock
    private MotivoRepository motivoRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;
    private Producto producto;
    private Motivo motivo;
    private Stock stock;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("admin", "pass", com.ventalen.auth.Rol.ADMIN);
        producto = new Producto("Producto", new java.math.BigDecimal("10.00"), new com.ventalen.categoria.Categoria("Cat"));
        motivo = new Motivo("Ingreso", true);
        stock = new Stock(producto, 5);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pass"));
    }

    @Test
    void test_crearAjustePositiveActualizaStock() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(productoService.buscarProductoConId(10L)).thenReturn(producto);
        when(motivoRepository.findById(1L)).thenReturn(Optional.of(motivo));
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.of(stock));
        when(ajusteStockRepository.save(any(AjusteStock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AjusteStock ajuste = ajusteStockService.crear(10L, 3, 1L, "ajuste");

        assertNotNull(ajuste);
        assertEquals(8, stock.getCantidad());
        verify(stockRepository).save(stock);
    }

    @Test
    void test_crearAjusteConMotivoInexistenteLanzaExcepcion() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(productoService.buscarProductoConId(10L)).thenReturn(producto);
        when(motivoRepository.findById(99L)).thenReturn(Optional.empty());

        ErrorMotivoInexistente exception = assertThrows(ErrorMotivoInexistente.class,
                () -> ajusteStockService.crear(10L, 3, 99L, "ajuste"));

        assertEquals("El motivo con id 99, no existe.", exception.getMessage());
    }

    @Test
    void test_obtenerTodosYBuscarPorId() {
        when(ajusteStockRepository.findAll()).thenReturn(List.of(new AjusteStock()));
        when(ajusteStockRepository.findById(1L)).thenReturn(Optional.of(new AjusteStock()));

        assertEquals(1, ajusteStockService.obtenerTodos().size());
        assertNotNull(ajusteStockService.buscarPorId(1L));
    }
}
