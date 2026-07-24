package com.ventalen.cliente;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import com.ventalen.exception.ErrorClienteInexistente;
import com.ventalen.exception.ErrorClienteYaExistente;

@ExtendWith(MockitoExtension.class)

public class ClienteServiceTest {
    @Mock 
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void test01_sePuedeGuardarUnClienteYSeRetornaElClienteGuardado(){
        String nyap = "Prueba";
        String telefono = "12345678";
        
        Cliente cliente = new Cliente(nyap, telefono);

        when(clienteRepository.existsByNyap(nyap.trim().toLowerCase())).thenReturn(false);

        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        
        Cliente clienteDelService = clienteService.crear(nyap, telefono);

        assertNotNull(clienteDelService);
        assertEquals(nyap.trim().toLowerCase(), clienteDelService.getNyap());

        verify(clienteRepository, times(1)).save(any(Cliente.class));

    }

    @Test
    void test02_noSePuedenCrearClientesConElMismoNyap(){
        when(clienteRepository.existsByNyap("prueba")).thenReturn(true);

        assertThrows(ErrorClienteYaExistente.class, () -> clienteService.crear("Prueba", "12345678"));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void test03_sePuedeModificarUnClienteCreado(){
        Cliente cliente = new Cliente("Prueba", "12345678");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente resultado = clienteService.modificar(1L, "Nuevo", "87654321");

        assertEquals("nuevo", resultado.getNyap());
        assertEquals("87654321", resultado.getTelefono());
        assertEquals(Boolean.TRUE, resultado.getActivo());
        verify(clienteRepository).save(cliente);
    }

    @Test
    void test04_noSePuedeModificarUnClienteQueNoExiste(){
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ErrorClienteInexistente.class,
                () -> clienteService.modificar(99L, "Nuevo", "87654321"));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void test05_sePuedeEliminarUnClienteCreado(){
        Cliente cliente = new Cliente("Prueba", "12345678");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        clienteService.eliminar(1L);

        assertEquals(Boolean.FALSE, cliente.getActivo());
        verify(clienteRepository).save(cliente);
    }

    @Test
    void test06_noSePuedeEliminarUnClienteQueNoExiste(){
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ErrorClienteInexistente.class, () -> clienteService.eliminar(99L));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }
}
