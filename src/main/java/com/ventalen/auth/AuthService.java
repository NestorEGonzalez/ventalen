package com.ventalen.auth;

import java.util.regex.Pattern;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ventalen.exception.ErrorUsuarioNoValido;
import com.ventalen.security.JwtUtil;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        Usuario usuario = usuarioRepository
                            .findByUsername(loginRequest.username())
                            .orElseThrow(()->{
                                throw new ErrorUsuarioNoValido(ErrorUsuarioNoValido.ERROR_USUARIO_INEXISTENTE);
                            });
        
        String token = jwtUtil.generarToken(usuario.getUsername(), usuario.getRol().name());

        return new AuthResponse(token, usuario.getUsername(), usuario.getRol().name());
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        validarPasswordSegura(registerRequest.password());
        verificarUsuarioExistente(registerRequest.username());
        Usuario nuevoUsuario = new Usuario(registerRequest.username(), passwordEncoder.encode(registerRequest.password()), registerRequest.rol());

        usuarioRepository.save(nuevoUsuario);

        String token = jwtUtil.generarToken(nuevoUsuario.getUsername(), nuevoUsuario.getRol().name());

        return new AuthResponse(token, nuevoUsuario.getUsername(), nuevoUsuario.getRol().name());
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ErrorUsuarioNoValido(ErrorUsuarioNoValido.ERROR_USUARIO_INEXISTENTE));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.currentPassword()));

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("La nueva contraseña y la confirmación deben coincidir.");
        }

        validarPasswordSegura(request.newPassword());

        if (passwordEncoder.matches(request.newPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña debe ser distinta a la actual.");
        }

        usuario.setPassword(passwordEncoder.encode(request.newPassword()));
        usuarioRepository.save(usuario);
    }

    private void validarPasswordSegura(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }

        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            throw new IllegalArgumentException("La contraseña debe incluir al menos una letra mayúscula.");
        }

        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            throw new IllegalArgumentException("La contraseña debe incluir al menos una letra minúscula.");
        }

        if (!Pattern.compile("\\d").matcher(password).find()) {
            throw new IllegalArgumentException("La contraseña debe incluir al menos un número.");
        }

        if (!Pattern.compile("[^A-Za-z0-9]").matcher(password).find()) {
            throw new IllegalArgumentException("La contraseña debe incluir al menos un carácter especial.");
        }
    }

    private void verificarUsuarioExistente(String username){
        if (usuarioRepository.existsByUsername(username.trim().toLowerCase())) {
            throw new ErrorUsuarioNoValido(ErrorUsuarioNoValido.ERROR_USUARIO_REGISTRADO);
        }
    }

    
}