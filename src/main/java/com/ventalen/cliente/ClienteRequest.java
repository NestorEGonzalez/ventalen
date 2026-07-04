package com.ventalen.cliente;

import com.ventalen.exception.ErrorCampoVacioONulo;

import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(
    @NotBlank(message = ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO)
    String nyap,
    String telefono
) {
}
