package com.ventalen.exception;

public class ErrorPrecioInvalido extends RuntimeException{
    public static final String ERROR_PRECIO_NULO = "El campo no puede ser nulo.";
    public static final String ERROR_PRECIO_NEGATIVO = "El precio no puede ser negativo.";

    public ErrorPrecioInvalido(String mensaje){
        super(mensaje);
    }
}
