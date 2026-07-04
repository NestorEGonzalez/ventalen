package com.ventalen.exception;

public class ErrorCantidadInvalida extends RuntimeException{
    public static final String ERROR_CANTIDAD_NULA = "La cantidad no puede ser nula.";
    public static final String ERROR_CANTIDAD_NEGATIVA = "La cantidad no puede ser negativa.";

    public ErrorCantidadInvalida(String mensaje){
        super(mensaje);
    }
}
