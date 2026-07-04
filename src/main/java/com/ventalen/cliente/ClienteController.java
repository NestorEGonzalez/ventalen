package com.ventalen.cliente;

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
@RequestMapping("/clientes")
public class ClienteController {

    private static final String URL_BASE = "/clientes";
    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;

    public ClienteController(ClienteService clienteService, ClienteMapper clienteMapper) {
        this.clienteService = clienteService;
        this.clienteMapper = clienteMapper;
    }

    @GetMapping(URL_BASE)
    public ResponseEntity<List<ClienteResponse>> obtenerTodos() {
        return ResponseEntity.ok(clienteMapper.toResponseList(clienteService.obtenerTodos()));
    }

    @GetMapping(URL_BASE + "/{id}")
    public ResponseEntity<ClienteResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteMapper.toResponse(clienteService.buscarPorId(id)));
    }

    @PostMapping(URL_BASE)
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody ClienteRequest request) {
        Cliente cliente = clienteService.crear(request.nyap(), request.telefono());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(cliente.getId()).toUri();
        return ResponseEntity.created(location).body(clienteMapper.toResponse(cliente));
    }

    @PatchMapping(URL_BASE + "/{id}")
    public ResponseEntity<ClienteResponse> modificar(@PathVariable Long id,
                                                     @Valid @RequestBody ClienteRequest request) {
        Cliente cliente = clienteService.modificar(id, request.nyap(), request.telefono());
        return ResponseEntity.accepted().body(clienteMapper.toResponse(cliente));
    }

    @DeleteMapping(URL_BASE + "/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
