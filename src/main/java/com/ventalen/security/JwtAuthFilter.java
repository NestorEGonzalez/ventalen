package com.ventalen.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter{ //OncePerRequestFilter asegura que solo se ejecute una vez por request.
    
    private final JwtUtil jwtUil; //Clase que maneja los jwt token

    public JwtAuthFilter(JwtUtil jwtUtil){
        this.jwtUil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        System.out.println(">> REQUEST: " + request.getMethod() + " " + request.getRequestURI());
        System.out.println(">> AUTH HEADER: " + request.getHeader("Authorization"));
        String authHeader = request.getHeader("Authorization"); //Busar el header "Authorization", el header debe tener el formato: Authorization: Bearer <token> es como un estandar

        if (authHeader == null || !authHeader.startsWith("Bearer")) { //Si no tiene header o no comienza con bearer lo manda a filterChain, es decir, deja pasar la request sin autorizacion
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); //Quita el prefijo Bearer 

        if (!jwtUil.esTokenValido(token)) { //Valida el token, si no es válido lo manda a filter chain, es decir, deja pasar la request sin autorizacion
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtUil.extraerUsername(token);
        String rol = jwtUil.extraerRol(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //Si existe el user name (distinto de nulo) y todavia no esta autenticado, crear un UserNamePasswordAuthenticationToken (crea el token de autenticacion)
            UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + rol)//Le asigna el rol)
                            ));
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); //Al token se le agregar detalles como IP y sesion (aunque como esta definido como stateles no tiene sesion, pero si sirve la ip.)
            SecurityContextHolder.getContext().setAuthentication(authToken); //En SecurityContextHolder es donde spring mantiene la info del usuario autenticado durante la request. 
        }

        filterChain.doFilter(request, response);
    }
}
