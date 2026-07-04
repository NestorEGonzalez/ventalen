package com.ventalen.exception;

public class ErrorClienteInexistente extends RuntimeException {
    public ErrorClienteInexistente(Long id) {
        super("El cliente con id " + id + ", no existe.");
    }
}
