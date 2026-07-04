package com.ventalen.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final long EXPIRATION_MS = 8*60*60*1000; //8 horas

    @Value("${app.jwt.secret}") //Trae la firma establecida en application.properties que va a firmar los tokens
    private String secretKey;
    
    //Convierte la clave secreta que es un string en un SecretKey
    private SecretKey getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //Crea un token en formato string
    public String generarToken(String username, String rol){
        return Jwts.builder()
                    .subject(username) //Usuario
                    .claim("rol", rol) //Rol del usuario
                    .issuedAt(new Date()) //fecha de creacion (el momento de la creacion)
                    .expiration(new Date(System.currentTimeMillis()+ EXPIRATION_MS)) //Fecha de vencimiento (fecha de creacion mas tiempo de expiracion)
                    .signWith(getSignKey()) //Firma secreta
                    .compact(); //Devuelve en formato compacto(en string);
    }

    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }
    
    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    //Intenta parsear el token, decodificar su estructura
    public boolean esTokenValido(String token) {
        try {
            extraerClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extrae los claims (pares clave valor del token)
    private Claims extraerClaims(String token) {
        return Jwts.parser() //decodifica el token
                    .verifyWith(getSignKey()) //verifia que esta firmado con la clave secreta
                    .build() //genera 
                    .parseSignedClaims(token)
                    .getPayload(); //extrae la info util user, expiracion o permisos
    }

    
}
