package com.ventalen.ingreso;

import java.time.LocalDate;
import java.util.List;

import com.ventalen.detalleIngreso.DetalleIngresoResponse;

public record IngresoResponse(
    Long id,
    LocalDate fecha,
    Long proveedorId,
    String proveedorNombre,
    String usuario,
    List<DetalleIngresoResponse> detalles
) {
}
