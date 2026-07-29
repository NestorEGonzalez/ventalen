package com.ventalen.ajuste;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import com.ventalen.TestBase;
import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaService;
import com.ventalen.motivo.Motivo;
import com.ventalen.motivo.MotivoRepository;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.stock.StockRepository;
import com.ventalen.stock.StockService;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class AjusteStockControllerTestIT extends TestBase {

    private static final String URL = "/ventalen/ajustes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private MotivoRepository motivoRepository;

    private Categoria categoria;
    private Producto producto;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin-test", ""));
        categoria = categoriaService.crearCategoria("Cat Controller");
        producto = productoService.crearProducto("Prod Controller", new BigDecimal("10.00"), categoria.getId());
    }

    @Test
    void test01_sePuedeCrearUnAjustePositivo() throws Exception {
        Motivo motivo = motivoRepository.save(new Motivo("Ingreso extra", true));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AjusteStockRequest(producto.getId(), 5, motivo.getId(), "detalle"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productoNombre").value("prod controller"))
                .andExpect(jsonPath("$.cantidad").value(5))
                .andExpect(header().exists("Location"));
    }

    @Test
    void test02_noSePuedeCrearUnAjusteConStockInsuficiente() throws Exception {
        stockService.crearStock(producto.getId(), 3);
        Motivo motivo = motivoRepository.save(new Motivo("Pérdida", false));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AjusteStockRequest(producto.getId(), 5, motivo.getId(), "sin stock"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test03_sePuedeObtenerTodosLosAjustes() throws Exception {
        Motivo motivo = motivoRepository.save(new Motivo("Ingreso extra", true));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AjusteStockRequest(producto.getId(), 2, motivo.getId(), "detalle 1"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AjusteStockRequest(producto.getId(), 3, motivo.getId(), "detalle 2"))))
                .andExpect(status().isCreated());

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
