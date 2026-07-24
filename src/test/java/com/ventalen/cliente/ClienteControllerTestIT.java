package com.ventalen.cliente;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.ventalen.TestBase;
import com.ventalen.exception.ErrorCampoVacioONulo;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class ClienteControllerTestIT extends TestBase {

    private static final String URL = "/ventalen/clientes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String nyap;
    private String telefono;

    @BeforeEach
    void setUp() {
        nyap = "Cliente Prueba";
        telefono = "12345678";
    }

    @Test
    void test01_sePuedeCrearUnCliente() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ClienteRequest(nyap, telefono))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nyap").value("cliente prueba"))
                .andExpect(header().exists("Location"));
    }

    @Test
    void test02_noSePuedenCrearClientesConElMismoNyap() throws Exception {
        crearCliente();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ClienteRequest(nyap, telefono))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("El cliente cliente prueba, ya existe."));
    }

    @Test
    void test03_sePuedeModificarUnClienteCreado() throws Exception {
        Cliente cliente = leerCliente(crearCliente().getResponse().getContentAsString());

        mockMvc.perform(patch(URL + "/" + cliente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ClienteRequest("Cliente Modificado", "87654321"))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(cliente.getId()))
                .andExpect(jsonPath("$.nyap").value("cliente modificado"))
                .andExpect(jsonPath("$.telefono").value("87654321"));
    }

    @Test
    void test04_noSePuedeModificarUnClienteQueNoExiste() throws Exception {
        mockMvc.perform(patch(URL + "/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ClienteRequest(nyap, telefono))))
                .andExpect(status().isNotFound());
    }

    @Test
    void test05_sePuedeEliminarUnClienteCreado() throws Exception {
        Cliente cliente = leerCliente(crearCliente().getResponse().getContentAsString());

        mockMvc.perform(delete(URL + "/" + cliente.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(URL + "/" + cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void test06_noSePuedeEliminarUnClienteQueNoExiste() throws Exception {
        mockMvc.perform(delete(URL + "/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void test07_sePuedeObtenerLaListaDeClientes() throws Exception {
        crearCliente();

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nyap").value("cliente prueba"));
    }

    @Test
    void test08_noSePuedeCrearUnClienteSinNyap() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ClienteRequest("", telefono))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO));
    }

    private MvcResult crearCliente() throws Exception {
        return mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ClienteRequest(nyap, telefono))))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private Cliente leerCliente(String json) throws Exception {
        return objectMapper.readValue(json, Cliente.class);
    }
}
