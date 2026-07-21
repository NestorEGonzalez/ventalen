package com.ventalen.motivo;

public record MotivoResponse(
    Long id, 
    String motivo, 
    Boolean afectaPositivo) {
}
