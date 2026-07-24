package com.ventalen.proveedor;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.ventalen.exception.ErrorProveedorInexistente;
import com.ventalen.exception.ErrorProveedorYaExistente;
import com.ventalen.security.JwtAuthFilter;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(value = ProveedorController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class ProveedorControllerTest {

    private static final String URL = "/ventalen/proveedores";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProveedorService proveedorService;

    @MockitoBean
    private ProveedorMapper proveedorMapper;

    private Proveedor proveedor;
    private ProveedorResponse response;

    @BeforeEach
    void setUp() {
        proveedor = new Proveedor("Proveedor Prueba", "Corredor", "12345678");
        response = new ProveedorResponse(1L, "proveedor prueba", "Corredor", "12345678");
    }

    @Test
    void test01_sePuedeCrearUnProveedor() throws Exception {
        when(proveedorService.crear("Proveedor Prueba", "Corredor", "12345678")).thenReturn(proveedor);
        when(proveedorMapper.toResponse(proveedor)).thenReturn(response);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProveedorRequest("Proveedor Prueba", "Corredor", "12345678"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.proveedor").value("proveedor prueba"))
                .andExpect(header().exists("Location"));
    }

    @Test
    void test02_noSePuedeCrearUnProveedorDuplicado() throws Exception {
        when(proveedorService.crear("Proveedor Prueba", "Corredor", "12345678"))
                .thenThrow(new ErrorProveedorYaExistente("proveedor prueba"));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProveedorRequest("Proveedor Prueba", "Corredor", "12345678"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("El proveedor proveedor prueba, ya existe."));
    }

    @Test
    void test03_sePuedeObtenerLaListaDeProveedores() throws Exception {
        when(proveedorService.obtenerTodos()).thenReturn(List.of(proveedor));
        when(proveedorMapper.toResponseList(anyList())).thenReturn(List.of(response));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].proveedor").value("proveedor prueba"));
    }

    @Test
    void test04_sePuedeModificarUnProveedor() throws Exception {
        when(proveedorService.modificar(1L, "Proveedor Nuevo", "Otro Corredor", "87654321"))
                .thenReturn(proveedor);
        when(proveedorMapper.toResponse(proveedor)).thenReturn(response);

        mockMvc.perform(patch(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProveedorRequest("Proveedor Nuevo", "Otro Corredor", "87654321"))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void test05_noSePuedeModificarUnProveedorInexistente() throws Exception {
        when(proveedorService.modificar(99L, "Proveedor Nuevo", "Corredor", "87654321"))
                .thenThrow(new ErrorProveedorInexistente(99L));

        mockMvc.perform(patch(URL + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProveedorRequest("Proveedor Nuevo", "Corredor", "87654321"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void test06_sePuedeEliminarUnProveedor() throws Exception {
        mockMvc.perform(delete(URL + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void test07_noSePuedeEliminarUnProveedorInexistente() throws Exception {
        doThrow(new ErrorProveedorInexistente(99L)).when(proveedorService).eliminar(99L);

        mockMvc.perform(delete(URL + "/99"))
                .andExpect(status().isNotFound());
    }
}
