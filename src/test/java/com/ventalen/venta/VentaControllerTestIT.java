package com.ventalen.venta;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.ventalen.TestBase;
import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaService;
import com.ventalen.cliente.Cliente;
import com.ventalen.cliente.ClienteService;
import com.ventalen.detalleVenta.DetalleVentaRequest;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class VentaControllerTestIT extends TestBase {

    private static final String URL = "/ventalen/ventas";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ClienteService clienteService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        Categoria cat = categoriaService.crearCategoria("Cat Test");
        producto = productoService.crearProducto("Prod Test", new BigDecimal("10.00"), cat.getId());
        stockRepository.save(new Stock(producto, 10));
    }

    @Test
    void test01_sePuedeCrearUnaVenta() throws Exception {
        DetalleVentaRequest detalle = new DetalleVentaRequest(producto.getId(), 2, new BigDecimal("15"));
        VentaRequest request = new VentaRequest(true, null, java.util.List.of(detalle));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pagado").value(true))
                .andExpect(header().exists("Location"));
    }

    @Test
    void test02_noSePuedeCrearUnaVentaConStockInsuficiente() throws Exception {
        DetalleVentaRequest detalle = new DetalleVentaRequest(producto.getId(), 50, new BigDecimal("15"));
        VentaRequest request = new VentaRequest(false, null, java.util.List.of(detalle));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test03_sePuedeObtenerTodasLasVentas() throws Exception {
        DetalleVentaRequest detalle = new DetalleVentaRequest(producto.getId(), 1, new BigDecimal("15"));
        VentaRequest request = new VentaRequest(true, null, java.util.List.of(detalle));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void test04_noSePuedeCrearUnaVentaConClienteInactivo() throws Exception {
        Cliente cliente = clienteService.crear("Cliente Inactivo", "12345678");
        clienteService.eliminar(cliente.getId());

        DetalleVentaRequest detalle = new DetalleVentaRequest(producto.getId(), 1, new BigDecimal("15"));
        VentaRequest request = new VentaRequest(true, cliente.getId(), java.util.List.of(detalle));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
