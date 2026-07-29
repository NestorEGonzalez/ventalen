package com.ventalen.venta;

import java.time.LocalDate;
import java.util.List;

import com.ventalen.detalleVenta.DetalleVentaResponse;

public record VentaResponse(
    Long id,
    LocalDate fecha,
    Boolean pagado,
    String usuario,
    Long clienteId,
    String clienteNombre,
    List<DetalleVentaResponse> detalles
) {
}
