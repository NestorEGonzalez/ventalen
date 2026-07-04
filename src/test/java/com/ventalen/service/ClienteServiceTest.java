package com.ventalen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ventalen.cliente.Cliente;
import com.ventalen.cliente.ClienteRepository;
import com.ventalen.cliente.ClienteService;
import com.ventalen.exception.ErrorClienteInexistente;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("Juan Pérez", "123456");
    }

    @Test
    void test_obtenerTodosRetornaListaDeClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        List<Cliente> clientes = clienteService.obtenerTodos();

        assertEquals(1, clientes.size());
        assertEquals("Juan Pérez", clientes.get(0).getNyap());
        verify(clienteRepository).findAll();
    }

    @Test
    void test_buscarPorIdLanzaExcepcionSiNoExiste() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        ErrorClienteInexistente exception = assertThrows(ErrorClienteInexistente.class,
                () -> clienteService.buscarPorId(99L));

        assertEquals("El cliente con id 99, no existe.", exception.getMessage());
    }

    @Test
    void test_crearClienteGuardaYDevuelveEntidad() {
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente creado = clienteService.crear("Juan Pérez", "123456", true);

        assertNotNull(creado);
        assertEquals("Juan Pérez", creado.getNyap());
        assertEquals("123456", creado.getTelefono());
        assertEquals(true, creado.getActivo());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void test_modificarClienteActualizaCampos() {
        Cliente existente = new Cliente("Ana", "111");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente modificado = clienteService.modificar(1L, "Luis", "222", false);

        assertEquals("Luis", modificado.getNyap());
        assertEquals("222", modificado.getTelefono());
        assertFalse(modificado.getActivo());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void test_eliminarClienteMarcaComoInactivo() {
        Cliente existente = new Cliente("Ana", "111");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        clienteService.eliminar(1L);

        assertFalse(existente.getActivo());
        verify(clienteRepository).save(any(Cliente.class));
    }
}
