package com.ventalen.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

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
import org.springframework.test.web.servlet.MockMvc;

import com.ventalen.categoria.Categoria;
import com.ventalen.exception.ErrorCampoVacioONulo;
import com.ventalen.exception.ErrorCategoriaInexistente;
import com.ventalen.exception.ErrorNombreProductoExistente;
import com.ventalen.exception.ErrorPrecioInvalido;
import com.ventalen.exception.ErrorProductoConIdInexistente;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoController;
import com.ventalen.producto.ProductoMapper;
import com.ventalen.producto.ProductoRequest;
import com.ventalen.producto.ProductoResponse;
import com.ventalen.producto.ProductoService;
import com.ventalen.security.JwtAuthFilter;

import tools.jackson.databind.ObjectMapper;
@WebMvcTest(value =ProductoController.class,excludeFilters = {
                                                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
})
@AutoConfigureMockMvc(addFilters = false)


public class ProductoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductoMapper productoMapper;

    private static final  String URL ="/productos/productos";

        private Long idProd;
        private String nombreProd;
        private BigDecimal precio;
        private Long idCat;
        private Categoria categoria;
        private Producto producto;
        private ProductoResponse productoResponse;

    @BeforeEach
    void setUp() throws Exception{
        idProd = 1L;
        nombreProd = "Producto 1";
        precio = new BigDecimal("10.00");
        idCat = 1L;
        categoria = new Categoria("Categoria 1");
        producto = new Producto(nombreProd, precio, categoria);
        productoResponse = new ProductoResponse(1L, nombreProd, precio, idCat);
    }

    @Test
    @DisplayName("Se puede crear una producto, se obtiene el producto y HTTP status 200.")
    void test_sePuedeCrearUnProductoYSeObtieneElProductoYStatus200() throws Exception{
        String request = objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCat));

        when(productoService.crearProducto(nombreProd, precio, idCat)).thenReturn(producto);
        when(productoMapper.toResponse(any(Producto.class))).thenReturn(productoResponse);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value(nombreProd))
                .andExpect(jsonPath("$.categoriaId").value(idCat))
                .andExpect(header().exists("Location"));

        verify(productoService).crearProducto(nombreProd, precio, idCat);
    }

    @Test
    @DisplayName("No se puede crear un producto con nombre nulo o vacío y se obtiene un HTTP status 400 ")
    void test_noSePuedeCrearUnProductoConNombreNuloOVacioYSeObtieneUnHTTPStatus400() throws Exception{
        String requestNulo = objectMapper.writeValueAsString(new ProductoRequest(null, precio, idCat));
        String requestVacio = objectMapper.writeValueAsString(new ProductoRequest("", precio, idCat));
        
        when(productoService.crearProducto(null, precio, idCat)).thenThrow(new ErrorCampoVacioONulo(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO));
        when(productoService.crearProducto("", precio, idCat)).thenThrow(new ErrorCampoVacioONulo(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNulo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestVacio))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO));
    }

    @Test
    @DisplayName("No se puede crear un producto con precio nulo o negativo y se obtiene un HTTP status 400 ")
    void test_noSePuedeCrearUnProductoConPrecioNuloONegativoYSeObtieneUnError() throws Exception{
        String requestPrecioNulo = objectMapper.writeValueAsString(new ProductoRequest(nombreProd, null, idCat));
        when(productoService.crearProducto(nombreProd, null, idCat))
                .thenThrow(new ErrorPrecioInvalido(ErrorPrecioInvalido.ERROR_PRECIO_NULO));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPrecioNulo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(ErrorPrecioInvalido.ERROR_PRECIO_NULO));

        BigDecimal precioNegativo = new BigDecimal("-1.00");
        String requestPrecioNegativo = objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precioNegativo, idCat));
        when(productoService.crearProducto(nombreProd, precioNegativo, idCat))
                .thenThrow(new ErrorPrecioInvalido(ErrorPrecioInvalido.ERROR_PRECIO_NEGATIVO));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPrecioNegativo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(ErrorPrecioInvalido.ERROR_PRECIO_NEGATIVO));
    }

    @Test
    @DisplayName("No se puede crear un producto con categoria nula o inexistente y se obtiene un HTTP status 400 ")
    void test_noSePuedeCrearUnProductoConCategoriaNulaOInexistenteYSeObtieneUnError() throws Exception{
        Long idCatInexistente = 99L;
        String request = objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCatInexistente));

        when(productoService.crearProducto(nombreProd, precio, idCatInexistente))
                .thenThrow(new ErrorCategoriaInexistente(idCatInexistente));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    @DisplayName("No se puede crear un producto con nombre ya existente y se obtiene un HTTP status 400 ")
    void test_noSePuedeCrearUnProductoConNombreYaExistenteYSeObtieneUnError() throws Exception{
        String request = objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCat));

        when(productoService.crearProducto(nombreProd, precio, idCat))
                .thenThrow(new ErrorNombreProductoExistente(nombreProd.trim().toLowerCase()));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("El producto " + nombreProd.trim().toLowerCase() + ", ya existe."));
    }

    @Test
    @DisplayName("Se puede borrar un producto por Id y se obtiene un HTTP status 204")
    void test_sePuedeBorrarUnProductoPorIdYSeObtieneUnHTTPStatus204() throws Exception{
        doNothing().when(productoService).borrarProductoConId(idProd);

        mockMvc.perform(delete(URL+"/"+idProd)).andExpect(status().isNoContent());

        verify(productoService).borrarProductoConId(idProd);
    }

    @Test
    @DisplayName("No se puede borrar un producto por Id si no existe y se obtiene un HTTP status 400")
    void test_noSePuedeBorrarUnProductoPorIdSiNoExisteYSeObtieneUnHTTPStatus400() throws Exception{
        Long idInexistente = 99L;

        doThrow(new ErrorProductoConIdInexistente(idInexistente))
                .when(productoService).borrarProductoConId(idInexistente);

        mockMvc.perform(delete(URL + "/" + idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje")
                        .value("El producto con id " + idInexistente + ", no existe."));  
    }

    @Test
    @DisplayName("Se puede cambiar el nombre de un producto y se obtiene el producto modificado junto a un HTTP status 202")
    void test_sePuedeCambiarElNombreDeUnProductoYSeObtieneUnHTTPStatus202() throws Exception{
        
        String nombreNuevo = "Producto Modificado";
        ProductoResponse responseModificado = new ProductoResponse(idProd, nombreNuevo, precio, idCat);

        String request = objectMapper.writeValueAsString(new ProductoRequest(nombreNuevo, responseModificado.getPrecio(), responseModificado.getCategoriaId()));

        when(productoMapper.toResponse(any(Producto.class))).thenReturn(responseModificado);
        when(productoService.modificarDatosDeProducto(idProd, nombreNuevo, precio, idCat)).thenReturn(producto);
        when(productoService.buscarProductoConId(idProd)).thenReturn(producto);

        mockMvc.perform(patch(URL + "/"+idProd)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.nombre").value(nombreNuevo));
    }

    @Test
    @DisplayName("No se puede cambiar el nombre de un producto si el nuevo es null o vacio o si ya existe y se obtiene un HTTP status 400")
    void test_noSePuedeCambiarElNombreDeUnProductoSiElNuevoEsNullOVacioOYaExisteYSeObtieneUnHTTPStatus400() throws Exception{
        String requestNulo = objectMapper.writeValueAsString(new ProductoRequest(null, precio,idCat));

        mockMvc.perform(patch(URL + "/"+idProd)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNulo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO));

        String nombreExistente = "Producto Existente";
        String requestExistente = objectMapper.writeValueAsString(new ProductoRequest(nombreExistente, precio, idCat));

        doThrow(new ErrorNombreProductoExistente(nombreExistente.trim().toLowerCase()))
                .when(productoService).modificarDatosDeProducto(idProd, nombreExistente, precio, idCat);

        mockMvc.perform(patch(URL + "/"+idProd)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestExistente))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje")
                        .value("El producto " + nombreExistente.trim().toLowerCase() + ", ya existe."));
    }

    @Test
    @DisplayName("Se puede cambiar el precio de un producto y se obtiene el producto modificado junto a un HTTP status 202")
    void test_sePuedeCambiarElPrecioDeUnProductoYSeObtieneUnHTTPStatus202() throws Exception{
        BigDecimal precioNuevo = new BigDecimal("99.99");
        ProductoResponse responseModificado = new ProductoResponse(idProd, nombreProd, precioNuevo, idCat);

        String request = objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precioNuevo, idCat));

        when(productoService.modificarDatosDeProducto(idProd, nombreProd, precioNuevo, idCat)).thenReturn(producto);
        when(productoService.buscarProductoConId(idProd)).thenReturn(producto);
        when(productoMapper.toResponse(any(Producto.class))).thenReturn(responseModificado);

        mockMvc.perform(patch(URL + "/"+idProd)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.precio").value(precioNuevo));
    }

    @Test
    @DisplayName("No se puede cambiar el precio de un producto si el nuevo es null o negativo y se obtiene un HTTP status 400")
    void test_noSePuedeCambiarElPrecioDeUnProductoSiEsNullONegativoYSeObtieneUnHTTPStatus202() throws Exception{
        String requestNulo = objectMapper.writeValueAsString(new ProductoRequest(nombreProd, null, idCat));

        doThrow(new ErrorPrecioInvalido(ErrorPrecioInvalido.ERROR_PRECIO_NULO))
                .when(productoService).modificarDatosDeProducto(idProd, nombreProd, null, idCat);

        mockMvc.perform(patch(URL + "/"+idProd)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNulo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(ErrorPrecioInvalido.ERROR_PRECIO_NULO));

        BigDecimal precioNegativo = new BigDecimal("-5.00");
        String requestNegativo = objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precioNegativo, idCat));

        doThrow(new ErrorPrecioInvalido(ErrorPrecioInvalido.ERROR_PRECIO_NEGATIVO))
                .when(productoService).modificarDatosDeProducto(idProd, nombreProd, precioNegativo, idCat);

        mockMvc.perform(patch(URL + "/"+idProd)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNegativo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(ErrorPrecioInvalido.ERROR_PRECIO_NEGATIVO));
    }

    @Test
    @DisplayName("Se puede cambiar la categoria de una producto y se obtiene el producto modificado junto a un HTTP status 202")
    void test_sePuedeCambiarLaCategoriaDeUnProductoYSeObtieneElProductoYUnHTTPStatus202() throws Exception{
        Long idCatNueva = 40L;
        Categoria catNueva = new Categoria("Nueva");
        Producto prodConCatModificada = new Producto(nombreProd, precio, catNueva);
        ProductoRequest prodRequest = new ProductoRequest(nombreProd, precio, idCatNueva);
        String request = objectMapper.writeValueAsString(prodRequest);
        ProductoResponse prodResponseModificado = new ProductoResponse(idProd, nombreProd, precio, idCatNueva);
        
        when(productoService.buscarProductoConId(idProd)).thenReturn(producto);
        when(productoService.modificarDatosDeProducto(anyLong(), anyString(), any(), anyLong())).thenReturn(prodConCatModificada);
        when(productoMapper.toResponse(any(Producto.class))).thenReturn(prodResponseModificado);
        
        mockMvc.perform(patch(URL + "/"+idProd)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.categoriaId").value(idCatNueva));
    }

    @Test
    @DisplayName("No Se puede cambiar la categoria de una producto si la misma no existe y se obtiene un HTTP status 400")
    void test_noSePuedeCambiarLaCategoriaDeUnProductoSiLaMismaNoExisteYSeObtieneUnHTTPStatus400() throws Exception{
        Long idCatInexistente = 99L;
        String request = objectMapper.writeValueAsString(new ProductoRequest(nombreProd, precio, idCatInexistente));

        doThrow(new ErrorCategoriaInexistente(idCatInexistente))
                .when(productoService).modificarDatosDeProducto(idProd, nombreProd, precio, idCatInexistente);

        mockMvc.perform(patch(URL + "/"+idProd)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").exists());
    }
}
