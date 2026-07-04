package com.ventalen.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
    @NotBlank(message = "El usuario no puede estar vacío.")
    String username,
    
    @NotBlank(message = "El password no puede estar vacío.")
    String password,
    
    @NotNull(message = "El rol no puede ser nulo.")
    Rol rol
){}
