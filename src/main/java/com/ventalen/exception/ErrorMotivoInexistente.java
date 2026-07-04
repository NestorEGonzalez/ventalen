package com.ventalen.exception;

public class ErrorMotivoInexistente extends RuntimeException {
    public ErrorMotivoInexistente(Long id) {
        super("El motivo con id " + id + ", no existe.");
    }
}
