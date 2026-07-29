package com.ventalen.exception;

public class ErrorProductoInactivo extends RuntimeException {
    public ErrorProductoInactivo(Long id) {
        super("El producto con id " + id + ", está inactivo.");
    }
}
