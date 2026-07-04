package com.ventalen.venta;

import java.time.LocalDate;
import java.util.List;

public record VentaResponse(
    Long id,
    LocalDate fecha,
    Boolean pagado,
    String usuario,
    List<DetalleVentaResponse> detalles
) {
}
