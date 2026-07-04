package com.ventalen.venta;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DetalleVentaRequest(
    @NotNull
    Long productoId,
    @NotNull @Positive
    Integer cantidad,
    @NotNull @Positive
    BigDecimal precioVenta
) {
}
