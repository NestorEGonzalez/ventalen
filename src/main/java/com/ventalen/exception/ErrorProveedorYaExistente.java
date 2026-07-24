package com.ventalen.exception;

public class ErrorProveedorYaExistente extends RuntimeException {
    public ErrorProveedorYaExistente(String proveedor) {
        super("El proveedor " + proveedor + ", ya existe.");
    }
}
