package com.ventalen.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "El usuario no puede estar vacío.")
    String username,

    @NotBlank(message = "El password no puede estar vacío.")
    String password
) {}
