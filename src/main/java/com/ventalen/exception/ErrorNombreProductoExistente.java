package com.ventalen.exception;

public class ErrorNombreProductoExistente extends RuntimeException {
    public ErrorNombreProductoExistente(String nombre){
        super("El producto "+nombre+", ya existe.");
    }
}
