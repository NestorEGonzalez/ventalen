package com.ventalen.funciones;

import com.ventalen.exception.ErrorCantidadInvalida;

public class ValidarCantidad {
    public static void validarCantidad(Integer cantidad){
        if (cantidad == null) {
            throw new IllegalArgumentException(ErrorCantidadInvalida.ERROR_CANTIDAD_NULA);
        }
        if (cantidad < 0) {
            throw new IllegalArgumentException(ErrorCantidadInvalida.ERROR_CANTIDAD_NEGATIVA);
        }
    }
}
