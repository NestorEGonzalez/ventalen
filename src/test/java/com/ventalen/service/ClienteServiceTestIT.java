package com.ventalen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ventalen.TestBase;
import com.ventalen.cliente.Cliente;
import com.ventalen.cliente.ClienteService;
import com.ventalen.exception.ErrorClienteInexistente;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class ClienteServiceTestIT extends TestBase {

    @Autowired
    private ClienteService clienteService;

    @Test
    void test_crearYBuscarClientePorId() {
        Cliente creado = clienteService.crear("Juan Pérez", "123456", true);

        Cliente encontrado = clienteService.buscarPorId(creado.getId());

        assertNotNull(encontrado);
        assertEquals("Juan Pérez", encontrado.getNyap());
        assertEquals("123456", encontrado.getTelefono());
        assertEquals(true, encontrado.getActivo());
    }

    @Test
    void test_modificarYEliminarCliente() {
        Cliente creado = clienteService.crear("Ana", "999", true);

        Cliente modificado = clienteService.modificar(creado.getId(), "Ana García", "888", false);
        clienteService.eliminar(modificado.getId());

        Cliente persistido = clienteService.buscarPorId(modificado.getId());

        assertEquals("Ana García", persistido.getNyap());
        assertFalse(persistido.getActivo());
    }

    @Test
    void test_buscarPorIdInexistenteLanzaExcepcion() {
        ErrorClienteInexistente exception = assertThrows(ErrorClienteInexistente.class,
                () -> clienteService.buscarPorId(999L));

        assertEquals("El cliente con id 999, no existe.", exception.getMessage());
    }
}
