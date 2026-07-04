package com.ventalen.exception;

public class ErrorVentaInexistente extends RuntimeException {
    public ErrorVentaInexistente(Long id) {
        super("La venta con id " + id + ", no existe.");
    }
}
