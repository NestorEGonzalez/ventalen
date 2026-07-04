package com.ventalen.exception;

public class ErrorStockInsuficiente extends RuntimeException {
    public ErrorStockInsuficiente(Long productoId, Integer disponible, Integer solicitado) {
        super("Stock insuficiente para el producto con id " + productoId
                + ". Disponible: " + disponible + ", solicitado: " + solicitado + ".");
    }
}
