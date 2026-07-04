package com.ventalen.funciones;

import java.math.BigDecimal;

import com.ventalen.exception.ErrorPrecioInvalido;

public class ValidarPrecio{
    public static BigDecimal validarPrecio(BigDecimal precio) {
        if (precio == null) {
            throw new ErrorPrecioInvalido(ErrorPrecioInvalido.ERROR_PRECIO_NULO);
        }
        BigDecimal cero = new BigDecimal("0");
        if (precio.compareTo(cero)<0) {
            throw new ErrorPrecioInvalido(ErrorPrecioInvalido.ERROR_PRECIO_NEGATIVO);
        }
        return precio;
    }
}
