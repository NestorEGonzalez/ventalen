package com.ventalen.proveedor;

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
class ProveedorControllerTestIT extends TestBase {

    private static final String URL = "/ventalen/proveedores";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String proveedor;
    private String corredor;
    private String telefono;

    @BeforeEach
    void setUp() {
        proveedor = "Proveedor Prueba";
        corredor = "Corredor";
        telefono = "12345678";
    }

    @Test
    void test01_sePuedeCrearUnProveedor() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProveedorRequest(proveedor, corredor, telefono))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.proveedor").value("proveedor prueba"))
                .andExpect(header().exists("Location"));
    }

    @Test
    void test02_noSePuedenCrearProveedoresConElMismoNombre() throws Exception {
        crearProveedor();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProveedorRequest(proveedor, corredor, telefono))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("El proveedor proveedor prueba, ya existe."));
    }

    @Test
    void test03_sePuedeModificarUnProveedorCreado() throws Exception {
        Proveedor creado = leerProveedor(crearProveedor().getResponse().getContentAsString());

        mockMvc.perform(patch(URL + "/" + creado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProveedorRequest("Proveedor Modificado", "Otro Corredor", "87654321"))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(creado.getId()))
                .andExpect(jsonPath("$.proveedor").value("proveedor modificado"))
                .andExpect(jsonPath("$.telefono").value("87654321"));
    }

    @Test
    void test04_noSePuedeModificarUnProveedorQueNoExiste() throws Exception {
        mockMvc.perform(patch(URL + "/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProveedorRequest(proveedor, corredor, telefono))))
                .andExpect(status().isNotFound());
    }

    @Test
    void test05_sePuedeEliminarUnProveedorCreado() throws Exception {
        Proveedor creado = leerProveedor(crearProveedor().getResponse().getContentAsString());

        mockMvc.perform(delete(URL + "/" + creado.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void test06_noSePuedeEliminarUnProveedorQueNoExiste() throws Exception {
        mockMvc.perform(delete(URL + "/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void test07_sePuedeObtenerLaListaDeProveedores() throws Exception {
        crearProveedor();

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].proveedor").value("proveedor prueba"));
    }

    @Test
    void test08_noSePuedeCrearUnProveedorSinNombre() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProveedorRequest("", corredor, telefono))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO));
    }

    private MvcResult crearProveedor() throws Exception {
        return mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProveedorRequest(proveedor, corredor, telefono))))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private Proveedor leerProveedor(String json) throws Exception {
        return objectMapper.readValue(json, Proveedor.class);
    }
}
