package com.ventalen.exception;

public class ErrorIngresoInexistente extends RuntimeException {
    public ErrorIngresoInexistente(Long id) {
        super("El ingreso con id " + id + ", no existe.");
    }
}
