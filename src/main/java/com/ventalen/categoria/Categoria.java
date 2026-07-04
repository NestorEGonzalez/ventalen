package com.ventalen.categoria;

import com.ventalen.exception.ErrorCampoVacioONulo;
import com.ventalen.funciones.ValidarString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO)
    @Column(unique = true, nullable = false)
    private String categoria;

    
    public Categoria(){}

    public Categoria(String nombre){
        this.categoria = ValidarString.validarString(nombre);

    }

    public void setCategoria(String nuevoNombre){
        this.categoria = ValidarString.validarString(nuevoNombre);

    }

    public String getCategoria(){
        return this.categoria;
    }

    public Long getId(){
        return this.id;
    }

    
    
}
