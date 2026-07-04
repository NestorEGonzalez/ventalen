package com.ventalen.categoria;

import com.ventalen.exception.ErrorCampoVacioONulo;

import jakarta.validation.constraints.NotBlank;


public record CategoriaRequest(
    @NotBlank(message = ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO)
    String categoria) {
} 
