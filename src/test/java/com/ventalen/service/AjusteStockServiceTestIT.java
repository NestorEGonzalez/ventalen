package com.ventalen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ventalen.TestBase;
import com.ventalen.ajuste.AjusteStock;
import com.ventalen.ajuste.AjusteStockService;
import com.ventalen.auth.Usuario;
import com.ventalen.auth.UsuarioRepository;
import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaService;
import com.ventalen.exception.ErrorMotivoInexistente;
import com.ventalen.motivo.Motivo;
import com.ventalen.motivo.MotivoRepository;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class AjusteStockServiceTestIT extends TestBase {

    @Autowired
    private AjusteStockService ajusteStockService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private MotivoRepository motivoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Producto producto;
    private Motivo motivo;

    @BeforeEach
    void setUp() {
        Usuario usuario = usuarioRepository.findByUsername("admin-test").orElseGet(() -> usuarioRepository.save(new Usuario("admin-test", "test4321", com.ventalen.auth.Rol.ADMIN)));
        Categoria categoria = categoriaService.crearCategoria("Categoría ajuste");
        producto = productoService.crearProducto("Producto ajuste", new BigDecimal("25.00"), categoria.getId());
        motivo = motivoRepository.save(new Motivo("Ingreso", true));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin-test", "test4321"));
    }

    @Test
    void test_crearAjusteYActualizarStock() {
        AjusteStock ajuste = ajusteStockService.crear(producto.getId(), 4, motivo.getId(), "ajuste de prueba");

        assertNotNull(ajuste);
        assertEquals(4, ajuste.getCantidad());
        assertEquals(producto.getId(), ajuste.getProducto().getId());
    }

    @Test
    void test_crearAjusteConMotivoInexistenteLanzaExcepcion() {
        ErrorMotivoInexistente exception = assertThrows(ErrorMotivoInexistente.class,
                () -> ajusteStockService.crear(producto.getId(), 1, 999L, "ajuste"));

        assertEquals("El motivo con id 999, no existe.", exception.getMessage());
    }
}
