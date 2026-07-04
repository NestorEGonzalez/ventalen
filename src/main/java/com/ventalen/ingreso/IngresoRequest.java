package com.ventalen.ingreso;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record IngresoRequest(
    @NotNull
    Long proveedorId,
    @NotEmpty @Valid
    List<DetalleIngresoRequest> detalles
) {
}
