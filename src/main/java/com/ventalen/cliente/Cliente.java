package com.ventalen.cliente;

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
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO)
    @Column(nullable = false)
    private String nyap;

    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;
    
    public Cliente(){}

    public Cliente(String nyap, String telefono){
        this.nyap = ValidarString.validarString(nyap);
        this.telefono = telefono;
        //this.activo = true;
    }

    public Long getId(){
        return this.id;
    }

    public String getNyap(){
        return this.nyap;
    }

    public String getTelefono(){
        return this.telefono;
    }

    public Boolean getActivo(){
        return this.activo;
    }

    public void setNyap(String nyap){
        this.nyap = ValidarString.validarString(nyap);
    }

    public void setTelefono(String telefono){
        this.telefono = telefono;
    }

    public void activarCliente(){
        this.activo = true;
    }

    public void desactivarCliente(){
        this.activo = false;
    }

}
