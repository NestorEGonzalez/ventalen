package com.ventalen.ingreso;

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
public class IngresoController {

    private static final String URL_BASE = "/ingresos";
    private final IngresoService ingresoService;
    private final IngresoMapper ingresoMapper;

    public IngresoController(IngresoService ingresoService, IngresoMapper ingresoMapper) {
        this.ingresoService = ingresoService;
        this.ingresoMapper = ingresoMapper;
    }

    @GetMapping(URL_BASE)
    public ResponseEntity<List<IngresoResponse>> obtenerTodos() {
        return ResponseEntity.ok(ingresoMapper.toResponseList(ingresoService.obtenerTodos()));
    }

    @GetMapping(URL_BASE + "/{id}")
    public ResponseEntity<IngresoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ingresoMapper.toResponse(ingresoService.buscarPorId(id)));
    }

    @PostMapping(URL_BASE)
    public ResponseEntity<IngresoResponse> crear(@Valid @RequestBody IngresoRequest request) {
        Ingreso ingreso = ingresoService.crear(request.proveedorId(), request.detalles());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(ingreso.getId()).toUri();
        return ResponseEntity.created(location).body(ingresoMapper.toResponse(ingreso));
    }
}
