package com.ventalen.venta;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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

import com.ventalen.detalleVenta.DetalleVentaRequest;
import com.ventalen.detalleVenta.DetalleVentaResponse;
import com.ventalen.exception.ErrorClienteInactivo;
import com.ventalen.exception.ErrorStockInsuficiente;
import com.ventalen.exception.ErrorVentaInexistente;
import com.ventalen.security.JwtAuthFilter;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(value = VentaController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class VentaControllerTest {

    private static final String URL = "/ventalen/ventas";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VentaService ventaService;

    @MockitoBean
    private VentaMapper ventaMapper;

    private VentaResponse response;

    @BeforeEach
    void setUp() {
        response = new VentaResponse(1L, LocalDate.now(), true, "admin",
                null, null, List.of());
    }

    @Test
    void test01_sePuedeCrearUnaVenta() throws Exception {
        Venta venta = new Venta();
        DetalleVentaRequest detalle = new DetalleVentaRequest(1L, 2, new BigDecimal("15"));
        when(ventaService.crear(eq(true), eq(null), anyList())).thenReturn(venta);
        when(ventaMapper.toResponse(any(Venta.class))).thenReturn(response);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new VentaRequest(true, null, List.of(detalle)))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(header().exists("Location"));
    }

    @Test
    void test02_noSePuedeCrearUnaVentaConStockInsuficiente() throws Exception {
        DetalleVentaRequest detalle = new DetalleVentaRequest(1L, 5, new BigDecimal("20"));
        when(ventaService.crear(eq(false), eq(null), anyList()))
                .thenThrow(new ErrorStockInsuficiente(1L, 2, 5));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new VentaRequest(false, null, List.of(detalle)))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(
                        "Stock insuficiente para el producto con id 1. Disponible: 2, solicitado: 5."));
    }

    @Test
    void test03_sePuedeObtenerTodasLasVentas() throws Exception {
        when(ventaService.obtenerTodas()).thenReturn(List.of());
        when(ventaMapper.toResponseList(anyList())).thenReturn(List.of());

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void test04_sePuedeObtenerUnaVentaPorId() throws Exception {
        when(ventaService.buscarPorId(1L)).thenReturn(new Venta());
        when(ventaMapper.toResponse(any(Venta.class))).thenReturn(response);

        mockMvc.perform(get(URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void test05_noSePuedeObtenerUnaVentaInexistente() throws Exception {
        doThrow(new ErrorVentaInexistente(99L)).when(ventaService).buscarPorId(99L);

        mockMvc.perform(get(URL + "/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void test06_noSePuedeCrearUnaVentaConClienteInactivo() throws Exception {
        DetalleVentaRequest detalle = new DetalleVentaRequest(1L, 1, new BigDecimal("15"));
        when(ventaService.crear(eq(true), eq(1L), anyList()))
                .thenThrow(new ErrorClienteInactivo(1L));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new VentaRequest(true, 1L, List.of(detalle)))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El cliente con id 1, está inactivo."));
    }

    @Test
    void test07_sePuedeCrearUnaVentaYNoPagarla() throws Exception {
        
    }
}
