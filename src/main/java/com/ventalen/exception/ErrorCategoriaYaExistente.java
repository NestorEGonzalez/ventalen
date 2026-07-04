package com.ventalen.exception;

public class ErrorCategoriaYaExistente extends RuntimeException {
    public ErrorCategoriaYaExistente(String nombre){
        super("La categoria "+ nombre + ", ya existe.");
    }
    
}
