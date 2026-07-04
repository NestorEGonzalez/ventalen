package com.ventalen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ventalen.TestBase;
import com.ventalen.exception.ErrorMotivoInexistente;
import com.ventalen.motivo.Motivo;
import com.ventalen.motivo.MotivoService;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class MotivoServiceTestIT extends TestBase {

    @Autowired
    private MotivoService motivoService;

    @Test
    void test_crearYBuscarMotivoPorId() {
        Motivo creado = motivoService.crear("Ingreso", true);

        Motivo encontrado = motivoService.buscarPorId(creado.getId());

        assertNotNull(encontrado);
        assertEquals("Ingreso", encontrado.getMotivo());
        assertEquals(true, encontrado.getAfectaPositivo());
    }

    @Test
    void test_modificarYEliminarMotivo() {
        Motivo creado = motivoService.crear("Egreso", false);

        Motivo modificado = motivoService.modificar(creado.getId(), "Ingreso", true);
        motivoService.eliminar(modificado.getId());

        ErrorMotivoInexistente exception = assertThrows(ErrorMotivoInexistente.class,
                () -> motivoService.buscarPorId(modificado.getId()));

        assertEquals("El motivo con id " + modificado.getId() + ", no existe.", exception.getMessage());
    }

    @Test
    void test_buscarPorIdInexistenteLanzaExcepcion() {
        ErrorMotivoInexistente exception = assertThrows(ErrorMotivoInexistente.class,
                () -> motivoService.buscarPorId(999L));

        assertEquals("El motivo con id 999, no existe.", exception.getMessage());
    }
}
