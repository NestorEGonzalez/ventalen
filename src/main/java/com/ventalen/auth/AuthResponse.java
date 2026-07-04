package com.ventalen.auth;

public record AuthResponse(
    String token,
    String username,
    String rol
) {}
