package com.ventalen.exception;

public class ErrorAjusteStockInexistente extends RuntimeException {
    public ErrorAjusteStockInexistente(Long id) {
        super("El ajuste de stock con id " + id + ", no existe.");
    }
}
