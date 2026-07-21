package com.ventalen.detalleIngreso;


import java.math.BigDecimal;

public record DetalleIngresoResponse(
    Long id,
    Long productoId,
    String productoNombre,
    Integer cantidad,
    BigDecimal precioMayorista
) {
}
