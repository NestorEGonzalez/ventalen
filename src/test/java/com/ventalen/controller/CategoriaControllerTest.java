package com.ventalen.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaController;
import com.ventalen.categoria.CategoriaMapper;
import com.ventalen.categoria.CategoriaRepository;
import com.ventalen.categoria.CategoriaRequest;
import com.ventalen.categoria.CategoriaResponse;
import com.ventalen.categoria.CategoriaService;
import com.ventalen.exception.ErrorCampoVacioONulo;
import com.ventalen.exception.ErrorCategoriaInexistente;
import com.ventalen.exception.ErrorCategoriaYaExistente;
import com.ventalen.security.JwtAuthFilter;

import tools.jackson.databind.ObjectMapper;


@WebMvcTest(value=CategoriaController.class, excludeFilters = {
                                                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
})
@AutoConfigureMockMvc(addFilters = false)

public class CategoriaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private CategoriaService categoriaService;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private CategoriaRequest categoriaRequest;

        @MockitoBean    
        private CategoriaMapper categoriaMapper;

        @MockitoBean
        private CategoriaRepository categoriaRepository;

        private static final String URL = "/productos/categorias";
        private String nombreCat1;
        private String nombreCat2;
        private Categoria cat1;
        private Categoria cat2;
        String errrorCat1Existente;
        String cat1Request;
        String catNullRequest;
        String catVacioRequest;
        CategoriaResponse cat1Response;
        CategoriaResponse cat2Response;

        @BeforeEach
        void setUp() throws Exception{
                nombreCat1 = "Categoria 1";
                nombreCat2 = "Categoria 2";
                cat1 = new Categoria(nombreCat1);
                cat2 = new Categoria(nombreCat2);
                errrorCat1Existente = "La categoria "+(nombreCat1.trim().toLowerCase())+", ya existe.";
                cat1Request =objectMapper.writeValueAsString(new CategoriaRequest(nombreCat1));
                cat1Response = new CategoriaResponse(1L, nombreCat1.trim().toLowerCase());
                cat2Response = new CategoriaResponse(2L, nombreCat2.trim().toLowerCase());
                catNullRequest = objectMapper.writeValueAsString(new CategoriaRequest(null));
                catVacioRequest = objectMapper.writeValueAsString(new CategoriaRequest(""));

        }


        @Test
        @DisplayName("Devuelve todas las categorias")
        
        void test_obtenerTodasLasCategorias() throws Exception{
                ReflectionTestUtils.setField(cat1,"id", 1L);
                ReflectionTestUtils.setField(cat2,"id", 2L);
                
                CategoriaResponse cat1Repsonse = new CategoriaResponse(1L, nombreCat1);
                CategoriaResponse cat2Repsonse = new CategoriaResponse(2L, nombreCat2);
                
                when(categoriaService.obtenerTodasLasCategorias()).thenReturn(List.of(cat1,cat2));
                when(categoriaMapper.toResponse(anyList())).thenReturn(List.of(cat1Repsonse,cat2Repsonse));

                mockMvc.perform(get(URL))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.size()").value(2));

                verify(categoriaService).obtenerTodasLasCategorias();

        }

        @Test
        @DisplayName("Crea una categoria")
        void test_crearUnaCategoria() throws Exception{
                when(categoriaMapper.toResponse(any(Categoria.class))).thenReturn(cat1Response);                
                when(categoriaService.crearCategoria(any())).thenReturn(cat1);

                mockMvc.perform(post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(cat1Request))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.id").isNumber())
                        .andExpect(jsonPath("$.categoria").value(nombreCat1.trim().toLowerCase()))
                        .andExpect(header().exists("Location"));

        }

        
        @Test
        @DisplayName("Al guardar una categoria existente se obtiene un error")
        void test_noSePuedeGuardarUnaCategoriaExistente() throws Exception{
                when(categoriaService.crearCategoria(nombreCat1)).thenThrow(new ErrorCategoriaYaExistente(nombreCat1.trim().toLowerCase()));

                mockMvc.perform(post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(cat1Request))
                        .andExpect(status().isConflict())
                        .andExpect(jsonPath("$.mensaje").value(errrorCat1Existente));
        }

        @Test
        @DisplayName("No se puede crear una categoria con nombre null o vacío")
        void test_noSePuedeCrearUnaCategoriaConNombreNullOVacio() throws Exception{
                when(categoriaService.crearCategoria(null)).thenThrow(new ErrorCampoVacioONulo(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO));
                
                mockMvc.perform(post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(catNullRequest))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.mensaje").value(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO));

                when(categoriaService.crearCategoria("")).thenThrow(new ErrorCampoVacioONulo(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO));

                mockMvc.perform(post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(catVacioRequest))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.mensaje").value(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO));
        }

        @Test
        @DisplayName("Borrar una categoria.")
        void test_borrarUnaCategoria() throws Exception{
                
                when(categoriaRepository.existsById(1L)).thenReturn(true);

                mockMvc.perform(delete(URL+"/1")).andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("No se puede borrar una categoria inexistente.")
        void test_noSePuedeBorrarUnaCategoriaInexistente() throws Exception{
                Long idInexistente = 99L;
                
                doThrow(new ErrorCategoriaInexistente(idInexistente)).when(categoriaService).eliminarCategoria(idInexistente);

                mockMvc.perform(delete(URL+"/"+idInexistente)).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Se puede Obtener una categoria por su id")
        void test_sePuedeObtenerUnaCategoriaPorId() throws Exception{
                when(categoriaMapper.toResponse(any(Categoria.class))).thenReturn(cat1Response);
                when(categoriaService.buscarCategoriaPorId(anyLong())).thenReturn(cat1);
                
                Long idExistente = 1L;
                mockMvc.perform(get(URL+"/"+idExistente))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.categoria").value(nombreCat1.trim().toLowerCase()));
        }

        @Test
        @DisplayName("Al buscar un id inexistente se obtiene un error")
        void test_alBuscarUnIdInexistenteSeObtieneUnError() throws Exception{
                
                Long idInexistente = 99L;
                String errorCatConIdInexistente = "La categoria con id: 99, no existe.";
                doThrow(new ErrorCategoriaInexistente(idInexistente)).when(categoriaService).buscarCategoriaPorId(idInexistente);
                
                mockMvc.perform(get(URL+"/"+idInexistente))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.mensaje").value(errorCatConIdInexistente));
        }

}
