package com.ventalen.detalleIngreso;


import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DetalleIngresoRequest(
    @NotNull
    Long productoId,
    @NotNull @Positive
    Integer cantidad,
    @NotNull @Positive
    BigDecimal precioMayorista
) {
}
