package com.ventalen.ingreso;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ventalen.auth.Usuario;
import com.ventalen.detalleIngreso.DetalleIngresoRequest;
import com.ventalen.exception.ErrorIngresoInexistente;
import com.ventalen.exception.ErrorProductoConIdInexistente;
import com.ventalen.exception.ErrorProveedorInexistente;
import com.ventalen.proveedor.Proveedor;
import com.ventalen.security.JwtAuthFilter;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(value = IngresoController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class IngresoControllerTest {

    private static final String URL = "/ventalen/ingresos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IngresoService ingresoService;

    @MockitoBean
    private IngresoMapper ingresoMapper;

    private Ingreso ingreso;
    private IngresoResponse response;

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        Proveedor proveedor = new Proveedor("Proveedor Prueba", "Corredor", "12345678");
        ingreso = new Ingreso(usuario, proveedor);
        response = new IngresoResponse(1L, LocalDate.now(), 1L, "proveedor prueba", "admin", List.of());
    }

    @Test
    void test01_sePuedeCrearUnIngreso() throws Exception {
        when(ingresoService.crear(any(Long.class), anyList())).thenReturn(ingreso);
        when(ingresoMapper.toResponse(any(Ingreso.class))).thenReturn(response);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new IngresoRequest(1L, List.of(
                                        new DetalleIngresoRequest(10L, 3, new BigDecimal("15.50")))))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.proveedorNombre").value("proveedor prueba"))
                .andExpect(header().exists("Location"));
    }

    @Test
    void test02_noSePuedeCrearUnIngresoConCantidadInvalida() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new IngresoRequest(1L, List.of(
                                        new DetalleIngresoRequest(10L, 0, new BigDecimal("15.50")))))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test03_noSePuedeCrearUnIngresoConProductoInexistente() throws Exception {
        when(ingresoService.crear(any(Long.class), anyList()))
                .thenThrow(new ErrorProductoConIdInexistente(99L));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new IngresoRequest(1L, List.of(
                                        new DetalleIngresoRequest(99L, 3, new BigDecimal("15.50")))))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El producto con id 99, no existe."));
    }

    @Test
    void test04_noSePuedeCrearUnIngresoConProveedorInexistente() throws Exception {
        when(ingresoService.crear(any(Long.class), anyList()))
                .thenThrow(new ErrorProveedorInexistente(99L));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new IngresoRequest(99L, List.of(
                                        new DetalleIngresoRequest(10L, 3, new BigDecimal("15.50")))))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El proveedor con id 99, no existe."));
    }

    @Test
    void test05_sePuedeObtenerTodosLosIngresos() throws Exception {
        when(ingresoService.obtenerTodos()).thenReturn(List.of(ingreso));
        when(ingresoMapper.toResponseList(anyList())).thenReturn(List.of(response));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].proveedorNombre").value("proveedor prueba"));
    }

    @Test
    void test06_sePuedeObtenerUnIngresoPorId() throws Exception {
        when(ingresoService.buscarPorId(1L)).thenReturn(ingreso);
        when(ingresoMapper.toResponse(ingreso)).thenReturn(response);

        mockMvc.perform(get(URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.proveedorNombre").value("proveedor prueba"));
    }

    @Test
    void test07_noSePuedeObtenerUnIngresoInexistente() throws Exception {
        when(ingresoService.buscarPorId(99L)).thenThrow(new ErrorIngresoInexistente(99L));

        mockMvc.perform(get(URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El ingreso con id 99, no existe."));
    }
}
