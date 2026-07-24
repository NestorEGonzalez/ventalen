package com.ventalen.auth;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
    @NotBlank(message = "La contraseña actual no puede estar vacía.")
    String currentPassword,

    @NotBlank(message = "La nueva contraseña no puede estar vacía.")
    String newPassword,

    @NotBlank(message = "La confirmación de la contraseña no puede estar vacía.")
    String confirmPassword
) {
}
