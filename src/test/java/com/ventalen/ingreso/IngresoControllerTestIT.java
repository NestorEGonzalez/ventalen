package com.ventalen.ingreso;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.ventalen.TestBase;
import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaService;
import com.ventalen.detalleIngreso.DetalleIngresoRequest;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.proveedor.Proveedor;
import com.ventalen.proveedor.ProveedorService;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class IngresoControllerTestIT extends TestBase {

    private static final String URL = "/ventalen/ingresos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private StockRepository stockRepository;

    private Proveedor proveedor;
    private Categoria categoria;
    private Producto producto;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin-test", ""));
        proveedor = proveedorService.crear("Test Prov", "Corredor", "123456");
        categoria = categoriaService.crearCategoria("Test Cat");
        producto = productoService.crearProducto("Test Prod", new BigDecimal("10.00"), categoria.getId());
    }

    @Test
    void test01_sePuedeCrearUnIngreso() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new IngresoRequest(proveedor.getId(), List.of(
                                        new DetalleIngresoRequest(producto.getId(), 3, new BigDecimal("15.50")))))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.proveedorNombre").value("test prov"))
                .andExpect(header().exists("Location"));
    }

    @Test
    void test02_noSePuedeCrearUnIngresoConCantidadInvalida() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new IngresoRequest(proveedor.getId(), List.of(
                                        new DetalleIngresoRequest(producto.getId(), 0, new BigDecimal("15.50")))))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test03_noSePuedeCrearUnIngresoConProductoInexistente() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new IngresoRequest(proveedor.getId(), List.of(
                                        new DetalleIngresoRequest(999L, 3, new BigDecimal("15.50")))))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El producto con id 999, no existe."));
    }

    @Test
    void test04_sePuedeObtenerTodosLosIngresos() throws Exception {
        crearIngreso(proveedor.getId(), List.of(
                new DetalleIngresoRequest(producto.getId(), 3, new BigDecimal("15.50"))));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].proveedorNombre").value("test prov"));
    }

    @Test
    void test05_noSePuedeObtenerUnIngresoInexistente() throws Exception {
        mockMvc.perform(get(URL + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El ingreso con id 999, no existe."));
    }

    private MvcResult crearIngreso(Long proveedorId, List<DetalleIngresoRequest> detalles) throws Exception {
        return mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new IngresoRequest(proveedorId, detalles))))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private IngresoResponse leerIngreso(String json) throws Exception {
        return objectMapper.readValue(json, IngresoResponse.class);
    }
}
