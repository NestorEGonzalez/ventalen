package com.ventalen.ajuste;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.ventalen.exception.ErrorAjusteStockInexistente;
import com.ventalen.exception.ErrorProductoInactivo;
import com.ventalen.exception.ErrorStockInsuficiente;
import com.ventalen.security.JwtAuthFilter;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(value = AjusteStockController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class AjusteStockControllerTest {

    private static final String URL = "/ventalen/ajustes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AjusteStockService ajusteStockService;

    @MockitoBean
    private AjusteStockMapper ajusteStockMapper;

    private AjusteStock ajusteStock;
    private AjusteStockResponse response;

    @BeforeEach
    void setUp() {
        ajusteStock = new AjusteStock();
        response = new AjusteStockResponse(1L, LocalDate.now(), 1L, "producto prueba",
                5, 1L, "ingreso", true, "admin", "detalle");
    }

    @Test
    void test01_sePuedeCrearUnAjustePositivo() throws Exception {
        when(ajusteStockService.crear(1L, 5, 1L, "detalle")).thenReturn(ajusteStock);
        when(ajusteStockMapper.toResponse(ajusteStock)).thenReturn(response);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AjusteStockRequest(1L, 5, 1L, "detalle"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cantidad").value(5))
                .andExpect(header().exists("Location"));
    }

    @Test
    void test02_noSePuedeCrearUnAjusteConStockInsuficiente() throws Exception {
        when(ajusteStockService.crear(1L, 100, 1L, "detalle"))
                .thenThrow(new ErrorStockInsuficiente(1L, 5, 100));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AjusteStockRequest(1L, 100, 1L, "detalle"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test03_sePuedeObtenerTodosLosAjustes() throws Exception {
        when(ajusteStockService.obtenerTodos()).thenReturn(List.of(ajusteStock));
        when(ajusteStockMapper.toResponseList(anyList())).thenReturn(List.of(response));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cantidad").value(5));
    }

    @Test
    void test04_sePuedeObtenerUnAjustePorId() throws Exception {
        when(ajusteStockService.buscarPorId(1L)).thenReturn(ajusteStock);
        when(ajusteStockMapper.toResponse(ajusteStock)).thenReturn(response);

        mockMvc.perform(get(URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void test05_noSePuedeObtenerUnAjusteInexistente() throws Exception {
        when(ajusteStockService.buscarPorId(99L)).thenThrow(new ErrorAjusteStockInexistente(99L));

        mockMvc.perform(get(URL + "/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void test06_noSePuedeCrearUnAjusteParaProductoInactivo() throws Exception {
        when(ajusteStockService.crear(1L, 5, 1L, "detalle"))
                .thenThrow(new ErrorProductoInactivo(1L));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AjusteStockRequest(1L, 5, 1L, "detalle"))))
                .andExpect(status().isBadRequest());
    }
}
