package com.ventalen.cliente;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import com.ventalen.exception.ErrorClienteInexistente;
import com.ventalen.exception.ErrorClienteYaExistente;
import com.ventalen.security.JwtAuthFilter;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(value = ClienteController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class ClienteControllerTest {

    private static final String URL = "/ventalen/clientes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClienteService clienteService;

    @MockitoBean
    private ClienteMapper clienteMapper;

    private Cliente cliente;
    private ClienteResponse response;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("Cliente Prueba", "12345678");
        response = new ClienteResponse(1L, "cliente prueba", "12345678", true);
    }

    @Test
    void test01_sePuedeCrearUnCliente() throws Exception {
        when(clienteService.crear("Cliente Prueba", "12345678")).thenReturn(cliente);
        when(clienteMapper.toResponse(cliente)).thenReturn(response);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ClienteRequest("Cliente Prueba", "12345678"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nyap").value("cliente prueba"))
                .andExpect(header().exists("Location"));
    }

    @Test
    void test02_noSePuedeCrearUnClienteDuplicado() throws Exception {
        when(clienteService.crear("Cliente Prueba", "12345678"))
                .thenThrow(new ErrorClienteYaExistente("cliente prueba"));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ClienteRequest("Cliente Prueba", "12345678"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("El cliente cliente prueba, ya existe."));
    }

    @Test
    void test03_sePuedeObtenerLaListaDeClientes() throws Exception {
        when(clienteService.obtenerTodos()).thenReturn(List.of(cliente));
        when(clienteMapper.toResponseList(anyList())).thenReturn(List.of(response));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nyap").value("cliente prueba"));
    }

    @Test
    void test04_sePuedeModificarUnCliente() throws Exception {
        when(clienteService.modificar(1L, "Cliente Nuevo", "87654321")).thenReturn(cliente);
        when(clienteMapper.toResponse(cliente)).thenReturn(response);

        mockMvc.perform(patch(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ClienteRequest("Cliente Nuevo", "87654321"))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void test05_noSePuedeModificarUnClienteInexistente() throws Exception {
        when(clienteService.modificar(99L, "Cliente Nuevo", "87654321"))
                .thenThrow(new ErrorClienteInexistente(99L));

        mockMvc.perform(patch(URL + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ClienteRequest("Cliente Nuevo", "87654321"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void test06_sePuedeEliminarUnCliente() throws Exception {
        mockMvc.perform(delete(URL + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void test07_noSePuedeEliminarUnClienteInexistente() throws Exception {
        doThrow(new ErrorClienteInexistente(99L)).when(clienteService).eliminar(99L);

        mockMvc.perform(delete(URL + "/99"))
                .andExpect(status().isNotFound());
    }
}
