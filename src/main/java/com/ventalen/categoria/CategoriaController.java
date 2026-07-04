package com.ventalen.categoria;

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
@RequestMapping("/ventalen")

public class CategoriaController {
    private static final String URL_BASE = "/categorias";

    private final CategoriaService categoriaService;
    private final CategoriaMapper categoriaMapper;
    
    public CategoriaController(CategoriaService categoriaService, CategoriaMapper categoriaMapper){
        this.categoriaService = categoriaService;
        this.categoriaMapper = categoriaMapper;
    }

    @GetMapping(URL_BASE)
    public ResponseEntity<List<CategoriaResponse>> obtenerCategorias(){
        List<CategoriaResponse> listaDeCategorias = categoriaMapper.toResponse(categoriaService.obtenerTodasLasCategorias());

        return ResponseEntity.ok(listaDeCategorias);
    }

    @PostMapping(URL_BASE)
    public ResponseEntity<CategoriaResponse> crearCategoria(@Valid @RequestBody CategoriaRequest categoria ){
        CategoriaResponse cat = categoriaMapper.toResponse(categoriaService.crearCategoria(categoria.categoria()));
        URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("{id}")
                        .buildAndExpand(cat.id())
                        .toUri();
        return ResponseEntity.created(location).body(cat);
    }

    @DeleteMapping(URL_BASE+"/{id}")
    public ResponseEntity<HttpStatus> borrarCategoria(@PathVariable Long id){
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(URL_BASE+"/{id}")
    public ResponseEntity<CategoriaResponse> modificarCategoria(@PathVariable Long id,@Valid @RequestBody CategoriaRequest categoria){
        CategoriaResponse cat = categoriaMapper.toResponse(categoriaService.cambiarNombreDeCategoriaPorId(id, categoria.categoria()));
        return ResponseEntity.ok(cat);
    }

    @GetMapping(URL_BASE+"/filtrar/{cat}")
    public ResponseEntity<List<CategoriaResponse>> filtrarCategorias(@Valid @PathVariable String cat){
        List<CategoriaResponse> cats = categoriaMapper.toResponse(categoriaService.buscarCategoriasQueContengan(cat));
        return ResponseEntity.ok(cats);
    }

    @GetMapping(URL_BASE+"/{id}")
    public ResponseEntity<CategoriaResponse> buscarCategoriaPorId(@PathVariable Long id){
        CategoriaResponse cat = categoriaMapper.toResponse(categoriaService.buscarCategoriaPorId(id));
        return ResponseEntity.ok(cat);
    }

}
