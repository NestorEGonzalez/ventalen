package com.ventalen.motivo;

import jakarta.validation.constraints.NotBlank;

public record MotivoRequest(
    @NotBlank
    String motivo,
    Boolean afectaPositivo
) {
}
