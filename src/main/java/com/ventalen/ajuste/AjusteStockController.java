package com.ventalen.ajuste;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ventalen")
public class AjusteStockController {

    private static final String URL_BASE = "/ajustes";
    private final AjusteStockService ajusteStockService;
    private final AjusteStockMapper ajusteStockMapper;

    public AjusteStockController(AjusteStockService ajusteStockService,
                                  AjusteStockMapper ajusteStockMapper) {
        this.ajusteStockService = ajusteStockService;
        this.ajusteStockMapper = ajusteStockMapper;
    }

    @GetMapping(URL_BASE)
    public ResponseEntity<List<AjusteStockResponse>> obtenerTodos() {
        return ResponseEntity.ok(ajusteStockMapper.toResponseList(ajusteStockService.obtenerTodos()));
    }

    @GetMapping(URL_BASE + "/{id}")
    public ResponseEntity<AjusteStockResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ajusteStockMapper.toResponse(ajusteStockService.buscarPorId(id)));
    }

    @PostMapping(URL_BASE)
    public ResponseEntity<AjusteStockResponse> crear(@Valid @RequestBody AjusteStockRequest request) {
        AjusteStock ajuste = ajusteStockService.crear(
                request.productoId(), request.cantidad(), request.motivoId(), request.detalles());
        URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(ajuste.getId()).toUri();
        return ResponseEntity.created(location).body(ajusteStockMapper.toResponse(ajuste));
    }
}
