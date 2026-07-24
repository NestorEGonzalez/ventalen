package com.ventalen.proveedor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ventalen.TestBase;
import com.ventalen.exception.ErrorProveedorInexistente;
import com.ventalen.exception.ErrorProveedorYaExistente;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class ProveedorServiceTestIT extends TestBase {

    @Autowired
    private ProveedorService proveedorService;

    private String proveedor;
    private String corredor;
    private String telefono;

    @BeforeEach
    void setUp() {
        proveedor = "Proveedor Prueba";
        corredor = "Corredor";
        telefono = "12345678";
    }

    @Test
    void test01_noSePuedenCrearProveedoresConElMismoNombre() {
        proveedorService.crear(proveedor, corredor, telefono);

        assertThrows(ErrorProveedorYaExistente.class,
                () -> proveedorService.crear(proveedor, corredor, telefono));
    }

    @Test
    void test02_sePuedeModificarUnProveedorCreado() {
        Proveedor creado = proveedorService.crear(proveedor, corredor, telefono);

        Proveedor modificado = proveedorService.modificar(creado.getId(), "Proveedor Modificado", "Otro Corredor", "87654321");

        assertEquals(creado.getId(), modificado.getId());
        assertEquals("proveedor modificado", modificado.getProveedor());
        assertEquals("Otro Corredor", modificado.getCorredor());
        assertEquals("87654321", modificado.getTelefono());
        assertEquals(Boolean.TRUE, modificado.getActivo());
    }

    @Test
    void test03_noSePuedeModificarUnProveedorQueNoExiste() {
        assertThrows(ErrorProveedorInexistente.class,
                () -> proveedorService.modificar(999L, "Proveedor Modificado", corredor, telefono));
    }

    @Test
    void test04_sePuedeEliminarUnProveedorCreado() {
        Proveedor creado = proveedorService.crear(proveedor, corredor, telefono);

        proveedorService.eliminar(creado.getId());

        assertEquals(0, proveedorService.obtenerTodos().size());
    }

    @Test
    void test05_noSePuedeEliminarUnProveedorQueNoExiste() {
        assertThrows(ErrorProveedorInexistente.class, () -> proveedorService.eliminar(999L));
    }
}
