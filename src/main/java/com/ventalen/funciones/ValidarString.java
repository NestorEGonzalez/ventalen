package com.ventalen.funciones;

import com.ventalen.exception.ErrorCampoVacioONulo;

public class ValidarString {
    
    public static String validarString(String string) {
        if (string == null ) {
            throw new ErrorCampoVacioONulo(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO);
            
        }
        if (string.isEmpty() || string.isBlank()) {
            throw new ErrorCampoVacioONulo(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO);
        }
        
        return string.trim().toLowerCase();
    }


}
