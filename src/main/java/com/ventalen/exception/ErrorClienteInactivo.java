package com.ventalen.exception;

public class ErrorClienteInactivo extends RuntimeException {
    public ErrorClienteInactivo(Long id) {
        super("El cliente con id " + id + ", está inactivo.");
    }
}
