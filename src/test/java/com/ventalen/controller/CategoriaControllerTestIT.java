package com.ventalen.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.ventalen.TestBase;
import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaRequest;
import com.ventalen.exception.ErrorCampoVacioONulo;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional

public class CategoriaControllerTestIT extends TestBase{
    
    @Autowired
    private MockMvc mockMvc;

     @Autowired
    private ObjectMapper objectMapper;

    
    String nombreCat;
    String otraCat;
    String errorCategoriaNulaOVacia;
    String errorCatExistente;
    String errorCatInexistente;
    Long idInexistente;
    private static final String URL_BASE = "/productos/categorias";
    
    @BeforeEach
    void setUp(){
        nombreCat = "Prueba";
        otraCat = "Otra categoria";
        idInexistente = 99L;
        errorCategoriaNulaOVacia = ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO;
        errorCatExistente ="La categoria "+ nombreCat.trim().toLowerCase() + ", ya existe.";
        errorCatInexistente = "La categoria con id: "+idInexistente+", no existe.";
    }

    @Test
    void test_sePuedeCrearUnaCategoriaYSeObtieneLaCategoriaYElCodigo201() throws Exception{
        mockMvc.perform(post(URL_BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new CategoriaRequest(nombreCat))))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.id").isNumber())
                        .andExpect(jsonPath("$.categoria").value(nombreCat.trim().toLowerCase()))
                        .andExpect(header().exists("Location"));
    }           


    @Test
    void test_noSeCreanCategoriasConElMismoNombreYSeObtieneUnError() throws Exception{
        mockMvc.perform(post(URL_BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new CategoriaRequest(nombreCat))))
                        .andExpect(status().isCreated());

        mockMvc.perform(post(URL_BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new CategoriaRequest(nombreCat))))
                        .andExpect(status().isConflict())
                        .andExpect(jsonPath("$.mensaje").value(errorCatExistente));
    }

    @Test
    void test_noSePuedeCrearUnaCategoriaConNombreNullOVacio() throws Exception{
        mockMvc.perform(post(URL_BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new CategoriaRequest(null))))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.mensaje").value(errorCategoriaNulaOVacia));
        
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoriaRequest(""))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensaje").value(errorCategoriaNulaOVacia));
    }

    @Test
    void test_sePuedenObtenerTodasLasCategoriasYUnHTTPStatusCode200() throws Exception{
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoriaRequest(nombreCat))))
                .andExpect(status().isCreated());

        mockMvc.perform(post(URL_BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new CategoriaRequest(otraCat))))
                        .andExpect(status().isCreated());

        mockMvc.perform(get(URL_BASE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].categoria", is(nombreCat.trim().toLowerCase())))
                .andExpect(jsonPath("$[1].categoria", is(otraCat.trim().toLowerCase())));

    }

    @Test
    void test_sePuedeEliminarUnaCategoriaExistenteYSeObtieneUnHTTPStatusCode200() throws Exception{
        String catGuardada = mockMvc.perform(post(URL_BASE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(new CategoriaRequest(nombreCat))))
                                    .andExpect(status().isCreated())
                                    .andReturn().getResponse().getContentAsString();
        
        Categoria categoria = objectMapper.readValue(catGuardada, Categoria.class);

        mockMvc.perform(get(URL_BASE).
                            contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$",hasSize(1)));

        mockMvc.perform(delete(URL_BASE+"/"+categoria.getId()))
                        .andExpect(status().isNoContent());

        mockMvc.perform(get(URL_BASE).
                    contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(0)));

    }

    @Test
    void test_noSePuedeEliminarUnaCategoriaInexistenteYSeObtieneUnError() throws Exception {
        mockMvc.perform(delete(URL_BASE+"/" + idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(errorCatInexistente));
    }

    @Test
    void test_sePuedeModificarUnaCategoriaExistenteYSeObtieneUnHTTPStatusCode200() throws Exception{
        String catGuardada = mockMvc.perform(post(URL_BASE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(new CategoriaRequest(nombreCat))))
                                    .andExpect(status().isCreated())
                                    .andReturn().getResponse().getContentAsString();

        Categoria cat = objectMapper.readValue(catGuardada, Categoria.class);

        mockMvc.perform(patch(URL_BASE+"/"+cat.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoriaRequest(otraCat))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.id").value(cat.getId()))
                    .andExpect(jsonPath("$.categoria").value(otraCat.trim().toLowerCase()));
                    
    }

    @Test
    void test_noSePuedeModificarUnaCategoriaInexistenteYSeObtieneUnError() throws Exception{
        mockMvc.perform(patch(URL_BASE+"/"+idInexistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoriaRequest(nombreCat))))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.mensaje").value(errorCatInexistente));
        
    }

    @Test
    void test_noSePuedeCambiarElNombreDeLaCategoriaSiEstaDuplicadoYSeObtieneUnError() throws Exception {    
        mockMvc.perform(post(URL_BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new CategoriaRequest(nombreCat))))
                        .andExpect(status().isCreated()); 
        
        String otraCategoriaString = mockMvc.perform(post(URL_BASE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(new CategoriaRequest(otraCat))))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString(); 
        
        Categoria categoria = objectMapper.readValue(otraCategoriaString, Categoria.class); 
        
        mockMvc.perform(patch(URL_BASE+"/" + categoria.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new CategoriaRequest(nombreCat))))
                        .andExpect(status().isConflict())
                        .andExpect(jsonPath("$.mensaje").value(errorCatExistente)); 
    
    }

    @Test
    void test_sePuedeFiltrarLasCategoriasQueContienenUnaPalabraEnSuNombre() throws Exception{
        mockMvc.perform(post(URL_BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new CategoriaRequest(nombreCat))))
                        .andExpect(status().isCreated()); 
        
        mockMvc.perform(post(URL_BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new CategoriaRequest(otraCat))))
                        .andExpect(status().isCreated()); 
        
        mockMvc.perform(post(URL_BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new CategoriaRequest("Categoria 2"))))
                        .andExpect(status().isCreated()); 
        
        String catABuscar = "catego";

        mockMvc.perform(get(URL_BASE+"/filtrar/"+catABuscar))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)));
    }

    @Test
    void test_sePuedeObtenerUnaCategoriaPorId() throws Exception{
        String otraCategoriaString = mockMvc.perform(post(URL_BASE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(new CategoriaRequest(otraCat))))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString(); 
        Categoria categoriaOtra = objectMapper.readValue(otraCategoriaString, Categoria.class); 

        String categoriaString = mockMvc.perform(post(URL_BASE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(new CategoriaRequest(nombreCat))))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString(); 
        Categoria categoria = objectMapper.readValue(categoriaString, Categoria.class); 

        mockMvc.perform(get(URL_BASE+"/"+categoria.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoria").value(categoria.getCategoria()));
        
        mockMvc.perform(get(URL_BASE+"/"+categoriaOtra.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoria").value(categoriaOtra.getCategoria()));

    }

    @Test
    void test_noSeObtieneUnaCategoriaSiSuIdEsInexistenteYSeObtieneUnError() throws Exception{
        mockMvc.perform(get(URL_BASE+"/"+idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(errorCatInexistente));

    }
     

}
