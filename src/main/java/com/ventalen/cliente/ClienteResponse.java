package com.ventalen.cliente;

public record ClienteResponse(
    Long id, 
    String nyap, 
    String telefono, 
    Boolean activo) {
}
