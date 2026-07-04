package com.ventalen.producto;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/productos")

public class ProductoController {
    private final ProductoService productoService;
    private final ProductoMapper productoMapper;
    private static final String URL_BASE = "/productos";

    public ProductoController(ProductoService productoService, ProductoMapper productoMapper){
        this.productoService = productoService;
        this.productoMapper = productoMapper;
    
    }

    @PostMapping(URL_BASE)
    public ResponseEntity<ProductoResponse> crearProducto(@RequestBody ProductoRequest producto){
        Producto prod = productoService.crearProducto(
                                        producto.nombre(),
                                        producto.precio(),
                                        producto.idCat());
        ProductoResponse productoFinal = productoMapper.toResponse(prod);

        URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("{id}")
                        .buildAndExpand(prod.getId())
                        .toUri();
        return ResponseEntity.created(location).body(productoFinal);
    }

    @GetMapping(URL_BASE)
    public ResponseEntity<List<ProductoResponse>> obtenerTodosLosProductos(){
        List<ProductoResponse> listaDeProductos = productoMapper.toResponseList(productoService.obtenerTodosLosProductos());

        return ResponseEntity.ok(listaDeProductos);
    }

    @DeleteMapping(URL_BASE+"/{id}")
    public ResponseEntity<HttpStatus> eliminarProductoPorId(@PathVariable Long id){
        productoService.borrarProductoConId(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping(URL_BASE+"/{id}")
    public ResponseEntity<ProductoResponse> modificarDatosDeProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequest productoRequest){
        ProductoResponse prodModificado = productoMapper.toResponse(productoService.modificarDatosDeProducto(id, productoRequest.nombre(), productoRequest.precio(), productoRequest.idCat()));
        return ResponseEntity.accepted().body(prodModificado);
    }

    @GetMapping(URL_BASE+"/{id}")
    public ResponseEntity<ProductoResponse> obtenerProductoPorId(@PathVariable Long id){
        ProductoResponse prod = productoMapper.toResponse(productoService.buscarProductoConId(id));
        return ResponseEntity.ok(prod);
    }

    @GetMapping(URL_BASE+"/filtrar/{palabra}")
    public ResponseEntity<List<ProductoResponse>> obtenerProductosConNombe(@PathVariable String palabra){
        List<ProductoResponse> filtro = productoMapper.toResponseList(productoService.obtenerProductosConNombre(palabra));
        return ResponseEntity.ok(filtro);
    }

    @GetMapping(URL_BASE+"/filtrarCategoria/{idCat}")
    public ResponseEntity <List<ProductoResponse>> obtenerProductosDeCategoria(@PathVariable Long idCat){
        List<ProductoResponse> filtroPorCat = productoMapper.toResponseList(productoService.obtenerProductosDeCategoria(idCat));
        return ResponseEntity.ok(filtroPorCat);
    }

    @PatchMapping(URL_BASE+"/{idCatVieja}/reemplazar/{idCatNueva}")
    public ResponseEntity<List<ProductoResponse>> actualizacionMasivaDeCategoria(@PathVariable Long idCatVieja, @PathVariable Long idCatNueva){
        productoService.actualizarCategoriaDeProductos(idCatVieja, idCatNueva);
        return ResponseEntity.accepted().build();
    }
}
