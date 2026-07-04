package com.ventalen.venta;

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
@RequestMapping("/ventas")
public class VentaController {

    private static final String URL_BASE = "/ventas";
    private final VentaService ventaService;
    private final VentaMapper ventaMapper;

    public VentaController(VentaService ventaService, VentaMapper ventaMapper) {
        this.ventaService = ventaService;
        this.ventaMapper = ventaMapper;
    }

    @GetMapping(URL_BASE)
    public ResponseEntity<List<VentaResponse>> obtenerTodas() {
        return ResponseEntity.ok(ventaMapper.toResponseList(ventaService.obtenerTodas()));
    }

    @GetMapping(URL_BASE + "/{id}")
    public ResponseEntity<VentaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaMapper.toResponse(ventaService.buscarPorId(id)));
    }

    @PostMapping(URL_BASE)
    public ResponseEntity<VentaResponse> crear(@Valid @RequestBody VentaRequest request) {
        Venta venta = ventaService.crear(request.pagado(), request.detalles());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(venta.getId()).toUri();
        return ResponseEntity.created(location).body(ventaMapper.toResponse(venta));
    }
}
