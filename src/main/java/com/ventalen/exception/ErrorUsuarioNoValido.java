package com.ventalen.exception;

public class ErrorUsuarioNoValido extends RuntimeException{
    public static final String ERROR_USUARIO_REGISTRADO = "El usuario ya se encuentra registrado.";

    public static final String ERROR_USUARIO_INEXISTENTE = "Usuario no registrado.";
    
    public ErrorUsuarioNoValido(String mensaje){
        super(mensaje);
    }
    
}
