package com.ventalen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ventalen.TestBase;
import com.ventalen.auth.AuthResponse;
import com.ventalen.auth.AuthService;
import com.ventalen.auth.LoginRequest;
import com.ventalen.auth.RegisterRequest;
import com.ventalen.auth.Rol;
import com.ventalen.auth.UsuarioRepository;
import com.ventalen.exception.ErrorUsuarioNoValido;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class AuthServiceTestIT extends TestBase {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void test_registerYLoginFuncionan() {
        AuthResponse response = authService.register(new RegisterRequest("nuevo-user", "pass123", Rol.USER));

        assertNotNull(response.token());
        assertEquals("nuevo-user", response.username());

        AuthResponse loginResponse = authService.login(new LoginRequest("nuevo-user", "pass123"));
        assertEquals("nuevo-user", loginResponse.username());
    }

    @Test
    void test_registerConUsuarioExistenteLanzaExcepcion() {
        authService.register(new RegisterRequest("duplicado", "pass123", Rol.USER));

        ErrorUsuarioNoValido exception = assertThrows(ErrorUsuarioNoValido.class,
                () -> authService.register(new RegisterRequest("duplicado", "pass123", Rol.USER)));

        assertEquals(ErrorUsuarioNoValido.ERROR_USUARIO_REGISTRADO, exception.getMessage());
    }
}
