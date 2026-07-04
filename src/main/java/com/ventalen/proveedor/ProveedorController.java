package com.ventalen.proveedor;

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
@RequestMapping("/proveedores")
public class ProveedorController {

    private static final String URL_BASE = "/proveedores";
    private final ProveedorService proveedorService;
    private final ProveedorMapper proveedorMapper;

    public ProveedorController(ProveedorService proveedorService, ProveedorMapper proveedorMapper) {
        this.proveedorService = proveedorService;
        this.proveedorMapper = proveedorMapper;
    }

    @GetMapping(URL_BASE)
    public ResponseEntity<List<ProveedorResponse>> obtenerTodos() {
        return ResponseEntity.ok(proveedorMapper.toResponseList(proveedorService.obtenerTodos()));
    }

    @GetMapping(URL_BASE + "/{id}")
    public ResponseEntity<ProveedorResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorMapper.toResponse(proveedorService.buscarPorId(id)));
    }

    @PostMapping(URL_BASE)
    public ResponseEntity<ProveedorResponse> crear(@Valid @RequestBody ProveedorRequest request) {
        Proveedor proveedor = proveedorService.crear(request.proveedor(), request.corredor(), request.telefono());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(proveedor.getId()).toUri();
        return ResponseEntity.created(location).body(proveedorMapper.toResponse(proveedor));
    }

    @PatchMapping(URL_BASE + "/{id}")
    public ResponseEntity<ProveedorResponse> modificar(@PathVariable Long id,
                                                      @Valid @RequestBody ProveedorRequest request) {
        Proveedor proveedor = proveedorService.modificar(id, request.proveedor(), request.corredor(), request.telefono());
        return ResponseEntity.accepted().body(proveedorMapper.toResponse(proveedor));
    }

    @DeleteMapping(URL_BASE + "/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proveedorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
