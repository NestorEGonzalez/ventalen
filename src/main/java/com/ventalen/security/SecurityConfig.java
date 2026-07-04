package com.ventalen.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//Indica a Spring que esta clase define la configuracion de seguridad
@Configuration 
@EnableWebSecurity //Activa el modulo de SpringSecurity

public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService; //Servicio que carga usuarios desde la base de datos.
    private final JwtAuthFilter jwtAuthFilter; // filtro personalizado que valida el token JWT en cada request
    private final static String URL = "/productos/**";
    private final static String ADMIN = "ADMIN";

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthFilter jwtAuthFilter){
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        System.out.println(">> SEGURIDAD CARGADA");
        httpSecurity
                    .csrf(csrf -> csrf.disable()) // desactiva CSRF, es deir esta API no utiliza sesiones de navegador (con cookis por ej)
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //indica que no se guarda estado en el server, todo se maneja con JWT token.
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/register").hasRole(ADMIN)   
                    .requestMatchers(HttpMethod.GET, URL).permitAll()
                    .requestMatchers(HttpMethod.POST, URL).hasRole(ADMIN)
                    .requestMatchers(HttpMethod.PATCH, URL).hasRole(ADMIN)
                    .requestMatchers(HttpMethod.DELETE, URL).hasRole(ADMIN)
                    .anyRequest().authenticated() //cualquier otro endpoint, requiere autenticacion. 
                )
                .authenticationProvider(authenticationProvider()) //registra el proveedor de autenticacion (que esta definido abajo)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); //Activa primero este filtro, para validar primero las peticiones mediante jwt.
        return httpSecurity.build();
    }
    
    //Beans auxiliares
    
    //Bean para encriptar contraseñas (el estandar)
    @Bean 
    public PasswordEncoder passwordEncoder(){ 
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService); //Se encargar de autenticar usuarios contra el userDetailsServiceImpl
        provider.setPasswordEncoder(passwordEncoder()); //Indica que use BCrypt para comparar contraseñas.
        return provider;
    }

    //Exponer el AuthenticationManager que coordina todo el proceso de autenticacion
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }


}
