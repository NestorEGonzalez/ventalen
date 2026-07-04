package com.ventalen.ajuste;

import jakarta.validation.constraints.NotNull;

public record AjusteStockRequest(
    @NotNull
    Long productoId,
    @NotNull
    Integer cantidad,
    @NotNull
    Long motivoId,
    String detalles
) {
}
