package com.ventalen.auth;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        
        verificarUsuarioExistente(registerRequest.username());
        Usuario nuevoUsuario = new Usuario(registerRequest.username(), passwordEncoder.encode(registerRequest.password()), registerRequest.rol());

        usuarioRepository.save(nuevoUsuario);

        String token = jwtUtil.generarToken(nuevoUsuario.getUsername(), nuevoUsuario.getRol().name());

        return new AuthResponse(token, nuevoUsuario.getUsername(), nuevoUsuario.getRol().name());
    }

    private void verificarUsuarioExistente(String username){
        if (usuarioRepository.existsByUsername(username.trim().toLowerCase())) {
            throw new ErrorUsuarioNoValido(ErrorUsuarioNoValido.ERROR_USUARIO_REGISTRADO);
        }
    }

    
}