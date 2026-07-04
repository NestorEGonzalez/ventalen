package com.ventalen.ajuste;

import java.time.LocalDate;

public record AjusteStockResponse(
    Long id,
    LocalDate fecha,
    Long productoId,
    String productoNombre,
    Integer cantidad,
    Long motivoId,
    String motivoNombre,
    Boolean afectaPositivo,
    String usuario,
    String detalles
) {
}
