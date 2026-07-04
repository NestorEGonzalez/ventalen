package com.ventalen.venta;

import java.math.BigDecimal;

public record DetalleVentaResponse(
    Long id,
    Long productoId,
    String productoNombre,
    Integer cantidad,
    BigDecimal precioVenta
) {
}
