package com.ventalen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ventalen.auth.AuthResponse;
import com.ventalen.auth.AuthService;
import com.ventalen.auth.LoginRequest;
import com.ventalen.auth.RegisterRequest;
import com.ventalen.auth.Rol;
import com.ventalen.auth.Usuario;
import com.ventalen.auth.UsuarioRepository;
import com.ventalen.exception.ErrorUsuarioNoValido;
import com.ventalen.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("admin", "encoded", Rol.ADMIN);
    }

    @Test
    void test_loginDevuelveTokenYUsuario() {
        LoginRequest request = new LoginRequest("admin", "pass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(new UsernamePasswordAuthenticationToken("admin", "pass"));
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(jwtUtil.generarToken("admin", Rol.ADMIN.name())).thenReturn("token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token", response.token());
        assertEquals("admin", response.username());
        verify(usuarioRepository).findByUsername("admin");
    }

    @Test
    void test_registerCreaUsuarioYDevuelveToken() {
        RegisterRequest request = new RegisterRequest("nuevo", "pass", Rol.USER);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(usuarioRepository.existsByUsername("nuevo")).thenReturn(false);
        when(jwtUtil.generarToken("nuevo", Rol.USER.name())).thenReturn("token");

        AuthResponse response = authService.register(request);

        assertEquals("token", response.token());
        assertEquals("nuevo", response.username());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void test_registerConUsuarioExistenteLanzaExcepcion() {
        RegisterRequest request = new RegisterRequest("admin", "pass", Rol.USER);
        when(usuarioRepository.existsByUsername("admin")).thenReturn(true);

        ErrorUsuarioNoValido exception = assertThrows(ErrorUsuarioNoValido.class,
                () -> authService.register(request));

        assertEquals(ErrorUsuarioNoValido.ERROR_USUARIO_REGISTRADO, exception.getMessage());
    }
}
