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
import com.ventalen.exception.ErrorProveedorInexistente;
import com.ventalen.ingreso.DetalleIngresoRequest;
import com.ventalen.ingreso.Ingreso;
import com.ventalen.ingreso.IngresoService;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.proveedor.Proveedor;
import com.ventalen.proveedor.ProveedorRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class IngresoServiceTestIT extends TestBase {

    @Autowired
    private IngresoService ingresoService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;
    private Proveedor proveedor;
    private Producto producto;

    @BeforeEach
    void setUp() {
        usuario = usuarioRepository.findByUsername("admin-test").orElseGet(() -> usuarioRepository.save(new Usuario("admin-test", "test4321", com.ventalen.auth.Rol.ADMIN)));
        proveedor = proveedorRepository.save(new Proveedor("Proveedor test", "Corredor", "123", true));
        Categoria categoria = categoriaService.crearCategoria("Categoría ingreso");
        producto = productoService.crearProducto("Producto ingreso", new BigDecimal("15.00"), categoria.getId());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin-test", "test4321"));
    }

    @Test
    void test_crearIngresoYActualizarStock() {
        Ingreso ingreso = ingresoService.crear(proveedor.getId(), List.of(new DetalleIngresoRequest(producto.getId(), 4, new BigDecimal("10.00"))));

        assertNotNull(ingreso);
        assertEquals(1, ingreso.getDetalles().size());
        assertEquals(4, ingreso.getDetalles().get(0).getCantidad());
    }

    @Test
    void test_crearIngresoConProveedorInexistenteLanzaExcepcion() {
        ErrorProveedorInexistente exception = assertThrows(ErrorProveedorInexistente.class,
                () -> ingresoService.crear(999L, List.of()));

        assertEquals("El proveedor con id 999, no existe.", exception.getMessage());
    }
}
