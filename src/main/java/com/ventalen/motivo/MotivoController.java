package com.ventalen.motivo;

import java.net.URI;
import java.util.List;

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
public class MotivoController {

    private static final String URL_BASE = "/motivos";
    private final MotivoService motivoService;
    private final MotivoMapper motivoMapper;

    public MotivoController(MotivoService motivoService, MotivoMapper motivoMapper) {
        this.motivoService = motivoService;
        this.motivoMapper = motivoMapper;
    }

    @GetMapping(URL_BASE)
    public ResponseEntity<List<MotivoResponse>> obtenerTodos() {
        return ResponseEntity.ok(motivoMapper.toResponseList(motivoService.obtenerTodos()));
    }

    @GetMapping(URL_BASE + "/{id}")
    public ResponseEntity<MotivoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(motivoMapper.toResponse(motivoService.buscarPorId(id)));
    }

    @PostMapping(URL_BASE)
    public ResponseEntity<MotivoResponse> crear(@Valid @RequestBody MotivoRequest request) {
        Motivo motivo = motivoService.crear(request.motivo(), request.afectaPositivo());
        URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(motivo.getId()).toUri();
        return ResponseEntity.created(location).body(motivoMapper.toResponse(motivo));
    }

    @PatchMapping(URL_BASE + "/{id}")
    public ResponseEntity<MotivoResponse> modificar(@PathVariable Long id,
                                                    @Valid @RequestBody MotivoRequest request) {
        Motivo motivo = motivoService.modificar(id, request.motivo());
        return ResponseEntity.accepted().body(motivoMapper.toResponse(motivo));
    }

    @DeleteMapping(URL_BASE + "/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        motivoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
