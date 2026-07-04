package com.ventalen.configuration;

import com.ventalen.auth.Rol;
import com.ventalen.auth.Usuario;
import com.ventalen.auth.UsuarioRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Initializer {

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repository.findByUsername(adminUsername).isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername(adminUsername);
                // ¡IMPORTANTE! Encripta la contraseña antes de guardar
                admin.setPassword(passwordEncoder.encode(adminPassword));
                
                admin.setRol(Rol.ADMIN);
                
                repository.save(admin);
                System.out.println("LOG: Usuario administrador creado con éxito: " + adminUsername);
            } else {
                System.out.println("LOG: El usuario administrador ya existe.");
            }
        };
    }
}