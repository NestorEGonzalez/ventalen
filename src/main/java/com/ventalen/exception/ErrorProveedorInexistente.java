package com.ventalen.exception;

public class ErrorProveedorInexistente extends RuntimeException {
    public ErrorProveedorInexistente(Long id) {
        super("El proveedor con id " + id + ", no existe.");
    }
}
