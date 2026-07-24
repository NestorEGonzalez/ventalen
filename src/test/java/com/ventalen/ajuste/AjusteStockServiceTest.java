package com.ventalen.ajuste;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.ventalen.exception.ErrorStockInsuficiente;
import com.ventalen.motivo.Motivo;
import com.ventalen.motivo.MotivoRepository;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;

@ExtendWith(MockitoExtension.class)
class AjusteStockServiceTest {

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

    @InjectMocks
    private AjusteStockService ajusteStockService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void noSePuedeDescontarStockSiNoHaySuficiente() {
        Usuario usuario = new Usuario();
        Producto producto = new Producto();
        Stock stockInicial = new Stock(producto, 3);
        Motivo motivo = new Motivo("Pérdida", false);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(productoService.buscarProductoConId(20L)).thenReturn(producto);
        when(motivoRepository.findById(2L)).thenReturn(Optional.of(motivo));
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.of(stockInicial));

        assertThrows(ErrorStockInsuficiente.class, () -> ajusteStockService.crear(20L, 5, 2L, "sin stock"));
    }

    @Test
    void ajustePositivoSumaStock() {
        Usuario usuario = new Usuario();
        Producto producto = new Producto();
        Stock stockInicial = new Stock(producto, 4);
        Motivo motivo = new Motivo("Ingreso extra", true);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(productoService.buscarProductoConId(21L)).thenReturn(producto);
        when(motivoRepository.findById(3L)).thenReturn(Optional.of(motivo));
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.of(stockInicial));
        when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ajusteStockRepository.save(any(AjusteStock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AjusteStock ajuste = ajusteStockService.crear(21L, 2, 3L, "sumar");

        assertEquals(6, stockInicial.getCantidad());
        verify(stockRepository).save(stockInicial);
        assertEquals("Ingreso extra", ajuste.getMotivo().getMotivo());
    }

    @Test
    void ajusteNegativoRestaStock() {
        Usuario usuario = new Usuario();
        Producto producto = new Producto();
        Stock stockInicial = new Stock(producto, 9);
        Motivo motivo = new Motivo("Merma", false);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pwd"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(productoService.buscarProductoConId(22L)).thenReturn(producto);
        when(motivoRepository.findById(4L)).thenReturn(Optional.of(motivo));
        when(stockRepository.findByProducto(producto)).thenReturn(Optional.of(stockInicial));
        when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ajusteStockRepository.save(any(AjusteStock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AjusteStock ajuste = ajusteStockService.crear(22L, 3, 4L, "restar");

        assertEquals(6, stockInicial.getCantidad());
        verify(stockRepository).save(stockInicial);
        assertEquals("Merma", ajuste.getMotivo().getMotivo());
    }
}
