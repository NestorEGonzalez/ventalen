package com.ventalen.exception;

public class ErrorClienteYaExistente extends RuntimeException {
    public ErrorClienteYaExistente(String nyap) {
        super("El cliente " + nyap + ", ya existe.");
    }
}
