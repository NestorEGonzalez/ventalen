package com.ventalen.cliente;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ventalen.TestBase;
import com.ventalen.exception.ErrorClienteInexistente;
import com.ventalen.exception.ErrorClienteYaExistente;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class ClienteServiceTestIT extends TestBase {

    @Autowired
    private ClienteService clienteService;

    private String nyap;
    private String telefono;

    @BeforeEach
    void setUp() {
        nyap = "Cliente Prueba";
        telefono = "12345678";
    }

    @Test
    void test01_noSePuedenCrearClientesConElMismoNyap() {
        clienteService.crear(nyap, telefono);

        assertThrows(ErrorClienteYaExistente.class, () -> clienteService.crear(nyap, telefono));
    }

    @Test
    void test02_sePuedeModificarUnClienteCreado() {
        Cliente cliente = clienteService.crear(nyap, telefono);

        Cliente modificado = clienteService.modificar(cliente.getId(), "Cliente Modificado", "87654321");

        assertEquals(cliente.getId(), modificado.getId());
        assertEquals("cliente modificado", modificado.getNyap());
        assertEquals("87654321", modificado.getTelefono());
        assertEquals(Boolean.TRUE, modificado.getActivo());
    }

    @Test
    void test03_noSePuedeModificarUnClienteQueNoExiste() {
        assertThrows(ErrorClienteInexistente.class,
                () -> clienteService.modificar(999L, "Cliente Modificado", telefono));
    }

    @Test
    void test04_sePuedeEliminarUnClienteCreado() {
        Cliente cliente = clienteService.crear(nyap, telefono);

        clienteService.eliminar(cliente.getId());

        Cliente eliminado = clienteService.buscarPorId(cliente.getId());
        assertEquals(Boolean.FALSE, eliminado.getActivo());
    }

    @Test
    void test05_noSePuedeEliminarUnClienteQueNoExiste() {
        assertThrows(ErrorClienteInexistente.class, () -> clienteService.eliminar(999L));
    }
}
