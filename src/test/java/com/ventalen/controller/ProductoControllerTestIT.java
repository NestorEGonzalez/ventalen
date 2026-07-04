package com.ventalen.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ventalen.TestBase;
import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaRequest;
import com.ventalen.categoria.CategoriaResponse;
import com.ventalen.exception.ErrorCampoVacioONulo;
import com.ventalen.exception.ErrorPrecioInvalido;
import com.ventalen.producto.ProductoRequest;
import com.ventalen.producto.ProductoResponse;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class ProductoControllerTestIT extends TestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String URL_BASE = "/productos/productos";
    private static final String URL_CATEGORIAS = "/productos/categorias";

    private String nombreProd;
    private String otroNombreProd;
    private BigDecimal precio;
    private BigDecimal otroPrecio;
    private Long idCatInexistente;
    private Long idProdInexistente;
    private String errorNombreNuloOVacio;
    private String errorPrecioNulo;
    private String errorPrecioNegativo;
    private Long idCat;

    @BeforeEach
    void setUp() throws Exception {
        nombreProd = "Un nombre";
        otroNombreProd = "Otro nombre";
        precio = new BigDecimal("10.00");
        otroPrecio = new BigDecimal("99.99");
        idCatInexistente = 99L;
        idProdInexistente = 99L;
        errorNombreNuloOVacio = ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO;
        errorPrecioNulo = ErrorPrecioInvalido.ERROR_PRECIO_NULO;
        errorPrecioNegativo = ErrorPrecioInvalido.ERROR_PRECIO_NEGATIVO;
        String catGuardada = mockMvc.perform(post(URL_CATEGORIAS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoriaRequest("Categoria Test"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        idCat = objectMapper.readValue(catGuardada, CategoriaResponse.class).id();
    }

    @Test
    void test_sePuedeCrearUnProductoYSeObtieneElProductoYStatus201() throws Exception {
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCat))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value(nombreProd.trim().toLowerCase()))
                .andExpect(jsonPath("$.categoriaId").value(idCat))
                .andExpect(header().exists("Location"));
    }

    @Test
    void test_noSePuedeCrearUnProductoConNombreNuloOVacioYSeObtieneUnHTTPStatus400() throws Exception {
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(null, precio, idCat))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(errorNombreNuloOVacio));

        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest("", precio, idCat))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(errorNombreNuloOVacio));
    }

    @Test
    void test_noSePuedeCrearUnProductoConPrecioNuloONegativoYSeObtieneUnHTTPStatus400() throws Exception {
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, null, idCat))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(errorPrecioNulo));

        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, new BigDecimal("-1.00"), idCat))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(errorPrecioNegativo));
    }

    @Test
    void test_noSePuedeCrearUnProductoConCategoriaInexistenteYSeObtieneUnHTTPStatus404() throws Exception {
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCatInexistente))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje")
                        .value("La categoria con id: " + idCatInexistente + ", no existe."));
    }

    @Test
    void test_noSePuedeCrearUnProductoConNombreYaExistenteYSeObtieneUnHTTPStatus409() throws Exception {
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCat))))
                .andExpect(status().isCreated());

        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCat))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje")
                        .value("El producto " + nombreProd.trim().toLowerCase() + ", ya existe."));
    }

    @Test
    void test_sePuedeBorrarUnProductoExistenteYSeObtieneUnHTTPStatus204() throws Exception {
        String prodGuardado = mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCat))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductoResponse producto = objectMapper.readValue(prodGuardado, ProductoResponse.class);

        mockMvc.perform(delete(URL_BASE + "/" + producto.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(URL_BASE + "/" + producto.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void test_noSePuedeBorrarUnProductoInexistenteYSeObtieneUnHTTPStatus404() throws Exception {
        mockMvc.perform(delete(URL_BASE + "/" + idProdInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje")
                        .value("El producto con id " + idProdInexistente + ", no existe."));
    }

    @Test
    void test_sePuedeCambiarElNombreDeUnProductoYSeObtieneElProductoModificadoYUnHTTPStatus202() throws Exception {
        String prodGuardado = mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCat))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductoResponse producto = objectMapper.readValue(prodGuardado, ProductoResponse.class);

        mockMvc.perform(patch(URL_BASE + "/" + producto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(otroNombreProd, producto.getPrecio(), producto.getCategoriaId()))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(producto.getId()))
                .andExpect(jsonPath("$.nombre").value(otroNombreProd.trim().toLowerCase()));
    }

    @Test
    void test_noSePuedeCambiarElNombreDeUnProductoSiElNuevoEsNuloOVacioYSeObtieneUnHTTPStatus400() throws Exception {
        String prodGuardado = mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCat))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductoResponse producto = objectMapper.readValue(prodGuardado, ProductoResponse.class);

        mockMvc.perform(patch(URL_BASE + "/" + producto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(null, precio, idCat))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(errorNombreNuloOVacio));

        mockMvc.perform(patch(URL_BASE + "/" + producto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest("", precio, idCat))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(errorNombreNuloOVacio));
    }

    @Test
    void test_noSePuedeCambiarElNombreDeUnProductoSiYaExisteYSeObtieneUnHTTPStatus409() throws Exception {
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCat))))
                .andExpect(status().isCreated());

        String prod2Guardado = mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(otroNombreProd, precio, idCat))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductoResponse producto2 = objectMapper.readValue(prod2Guardado, ProductoResponse.class);

        mockMvc.perform(patch(URL_BASE + "/" + producto2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCat))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje")
                        .value("El producto " + nombreProd.trim().toLowerCase() + ", ya existe."));
    }

    @Test
    void test_sePuedeCambiarElPrecioDeUnProductoYSeObtieneElProductoModificadoYUnHTTPStatus202() throws Exception {
        String prodGuardado = mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCat))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductoResponse producto = objectMapper.readValue(prodGuardado, ProductoResponse.class);

        mockMvc.perform(patch(URL_BASE + "/" + producto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(producto.getNombre(), otroPrecio, producto.getCategoriaId()))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(producto.getId()))
                .andExpect(jsonPath("$.precio").value(otroPrecio));
    }

    @Test
    void test_noSePuedeCambiarElPrecioDeUnProductoSiEsNuloONegativoYSeObtieneUnHTTPStatus400() throws Exception {
        String prodGuardado = mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCat))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductoResponse producto = objectMapper.readValue(prodGuardado, ProductoResponse.class);

        mockMvc.perform(patch(URL_BASE + "/" + producto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, null, idCat))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(errorPrecioNulo));

        mockMvc.perform(patch(URL_BASE + "/" + producto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, new BigDecimal("-5.00"), idCat))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(errorPrecioNegativo));
    }

    @Test
    void test_sePuedeCambiarLaCategoriaDeUnProductoYSeObtieneElProductoModificadoYUnHTTPStatus202() throws Exception {
        String cat2Guardada = mockMvc.perform(post(URL_CATEGORIAS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoriaRequest("Categoria 2"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long idCat2 = objectMapper.readValue(cat2Guardada, CategoriaResponse.class).id();

        String prodGuardado = mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCat))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductoResponse producto = objectMapper.readValue(prodGuardado, ProductoResponse.class);

        mockMvc.perform(patch(URL_BASE + "/" + producto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCat2))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(producto.getId()))
                .andExpect(jsonPath("$.categoriaId").value(idCat2));
    }

    @Test
    void test_noSePuedeCambiarLaCategoriaDeUnProductoSiLaMismaNoExisteYSeObtieneUnHTTPStatus404() throws Exception {
        String prodGuardado = mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCat))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductoResponse producto = objectMapper.readValue(prodGuardado, ProductoResponse.class);

        mockMvc.perform(patch(URL_BASE + "/" + producto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCatInexistente))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje")
                        .value("La categoria con id: " + idCatInexistente + ", no existe."));
    }

    @Test
    void test_sePuedenObtenerTodosLosProductosYUnHTTPStatus200() throws Exception{
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest("Producto 1", precio, idCat))));
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest("Producto 2", precio, idCat))));
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest("Producto 3", precio, idCat))));
        
        mockMvc.perform(get(URL_BASE)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3));
                
    }

    @Test
    void test_sePuedenObtenerTodosLosProductosQueContengaUnaPalabraEnSuNombre() throws Exception{
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest("Yerba", precio, idCat))));
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest("Yerba de prueba", precio, idCat))));
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest("Mate", precio, idCat))));

        mockMvc.perform(get(URL_BASE+"/filtrar/yerba"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        mockMvc.perform(get(URL_BASE+"/filtrar/mate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
        mockMvc.perform(get(URL_BASE+"/filtrar/producto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void test_sePuedenObtenerTodosLosProductosDeQueSeanDeUnaCategoria() throws Exception{
        String categoria2 =     mockMvc.perform(post(URL_CATEGORIAS)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(new CategoriaRequest("Categoria 2"))))
                                        .andReturn().getResponse().getContentAsString();
        
        Long idCat2 = objectMapper.readValue(categoria2, Categoria.class).getId();

        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest("Yerba", precio, idCat))));
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest("Yerba de prueba", precio, idCat2))));
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest("Mate", precio, idCat2))));
        
        mockMvc.perform(get(URL_BASE+"/filtrarCategoria/"+idCat2))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.size()").value(2));

    }

    @Test
    void test_sePuedeHacerCambiarTodosLosProductosDeUnaCategoriaPorOtraExistente() throws Exception{
        String categoria2 =     mockMvc.perform(post(URL_CATEGORIAS)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(new CategoriaRequest("Categoria 2"))))
                                        .andReturn().getResponse().getContentAsString();
        
        Long idCat2 = objectMapper.readValue(categoria2, Categoria.class).getId();

        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest("Yerba", precio, idCat))));
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest("Yerba de prueba", precio, idCat2))));
        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoRequest("Mate", precio, idCat2))));
        
        mockMvc.perform(patch(URL_BASE+"/"+idCat2+"/reemplazar/"+idCat))
                .andExpect(status().isAccepted());
        
        mockMvc.perform(get(URL_BASE+"/filtrarCategoria/"+idCat2))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.size()").value(0));
        mockMvc.perform(get(URL_BASE+"/filtrarCategoria/"+idCat))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.size()").value(3));
    }
}