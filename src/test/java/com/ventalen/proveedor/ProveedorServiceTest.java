package com.ventalen.proveedor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ventalen.exception.ErrorProveedorInexistente;
import com.ventalen.exception.ErrorProveedorYaExistente;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorService proveedorService;

    @Test
    void test01_sePuedeGuardarUnProveedorYSeRetornaElProveedorGuardado() {
        Proveedor proveedor = new Proveedor("Proveedor Uno", "Corredor", "12345678");
        when(proveedorRepository.existsByProveedor("proveedor uno")).thenReturn(false);
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor);

        Proveedor resultado = proveedorService.crear("Proveedor Uno", "Corredor", "12345678");

        assertNotNull(resultado);
        assertEquals("proveedor uno", resultado.getProveedor());
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }

    @Test
    void test02_noSePuedenCrearProveedoresConElMismoNombre() {
        when(proveedorRepository.existsByProveedor("proveedor uno")).thenReturn(true);

        assertThrows(ErrorProveedorYaExistente.class,
                () -> proveedorService.crear("Proveedor Uno", "Corredor", "12345678"));

        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    void test03_sePuedeModificarUnProveedorCreado() {
        Proveedor proveedor = new Proveedor("Proveedor Uno", "Corredor", "12345678");
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(proveedorRepository.save(proveedor)).thenReturn(proveedor);

        Proveedor resultado = proveedorService.modificar(1L, "Proveedor Nuevo", "Otro Corredor", "87654321");

        assertEquals("proveedor nuevo", resultado.getProveedor());
        assertEquals("Otro Corredor", resultado.getCorredor());
        assertEquals("87654321", resultado.getTelefono());
        assertEquals(Boolean.TRUE, resultado.getActivo());
        verify(proveedorRepository).save(proveedor);
    }

    @Test
    void test04_noSePuedeModificarUnProveedorQueNoExiste() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ErrorProveedorInexistente.class,
                () -> proveedorService.modificar(99L, "Proveedor Nuevo", "Corredor", "87654321"));

        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    void test05_sePuedeEliminarUnProveedorCreado() {
        Proveedor proveedor = new Proveedor("Proveedor Uno", "Corredor", "12345678");
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));

        proveedorService.eliminar(1L);

        assertEquals(Boolean.FALSE, proveedor.getActivo());
        verify(proveedorRepository).save(proveedor);
    }

    @Test
    void test06_noSePuedeEliminarUnProveedorQueNoExiste() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ErrorProveedorInexistente.class, () -> proveedorService.eliminar(99L));

        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }
}
