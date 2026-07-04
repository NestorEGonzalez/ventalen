package com.ventalen.proveedor;

import com.ventalen.exception.ErrorCampoVacioONulo;

import jakarta.validation.constraints.NotBlank;

public record ProveedorRequest(
    @NotBlank(message = ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO)
    String proveedor,
    String corredor,
    String telefono
) {
}
