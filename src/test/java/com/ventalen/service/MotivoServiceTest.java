package com.ventalen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.ventalen.exception.ErrorMotivoInexistente;
import com.ventalen.motivo.Motivo;
import com.ventalen.motivo.MotivoRepository;
import com.ventalen.motivo.MotivoService;

@ExtendWith(MockitoExtension.class)
class MotivoServiceTest {

    @InjectMocks
    private MotivoService motivoService;

    @Mock
    private MotivoRepository motivoRepository;

    private Motivo motivo;

    @BeforeEach
    void setUp() {
        motivo = new Motivo("Ingreso", true);
    }

    @Test
    void test_obtenerTodosRetornaListaDeMotivos() {
        when(motivoRepository.findAll()).thenReturn(List.of(motivo));

        List<Motivo> motivos = motivoService.obtenerTodos();

        assertEquals(1, motivos.size());
        assertEquals("Ingreso", motivos.get(0).getMotivo());
        verify(motivoRepository).findAll();
    }

    @Test
    void test_buscarPorIdLanzaExcepcionSiNoExiste() {
        when(motivoRepository.findById(99L)).thenReturn(Optional.empty());

        ErrorMotivoInexistente exception = assertThrows(ErrorMotivoInexistente.class,
                () -> motivoService.buscarPorId(99L));

        assertEquals("El motivo con id 99, no existe.", exception.getMessage());
    }

    @Test
    void test_crearMotivoGuardaYDevuelveEntidad() {
        when(motivoRepository.save(any(Motivo.class))).thenReturn(motivo);

        Motivo creado = motivoService.crear("Ingreso", true);

        assertNotNull(creado);
        assertEquals("Ingreso", creado.getMotivo());
        assertEquals(true, creado.getAfectaPositivo());
        verify(motivoRepository).save(any(Motivo.class));
    }

    @Test
    void test_modificarMotivoActualizaCampos() {
        Motivo existente = new Motivo("Egreso", false);
        when(motivoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(motivoRepository.save(any(Motivo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Motivo modificado = motivoService.modificar(1L, "Ingreso", true);

        assertEquals("Ingreso", modificado.getMotivo());
        assertEquals(true, modificado.getAfectaPositivo());
        verify(motivoRepository).save(any(Motivo.class));
    }

    @Test
    void test_eliminarMotivoLanzaExcepcionSiNoExiste() {
        when(motivoRepository.existsById(99L)).thenReturn(false);

        ErrorMotivoInexistente exception = assertThrows(ErrorMotivoInexistente.class,
                () -> motivoService.eliminar(99L));

        assertEquals("El motivo con id 99, no existe.", exception.getMessage());
    }
}
