package com.ventalen.venta;

import java.util.List;

import com.ventalen.detalleVenta.DetalleVentaRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record VentaRequest(
    Boolean pagado,
    @NotEmpty @Valid
    List<DetalleVentaRequest> detalles
) {
}
