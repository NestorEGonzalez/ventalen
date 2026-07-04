package com.ventalen.proveedor;

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
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO)
    @Column(nullable = false)
    private String proveedor;

    private String corredor;

    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;

    public Proveedor(){}

    public Proveedor(String proveedor, String corredor, String telefono){
        this.proveedor = ValidarString.validarString(proveedor);
        this.corredor = corredor;
        this.telefono = telefono;
        this.activo = true;
    }

    public Long getId(){
        return this.id;
    }

    public String getProveedor(){
        return this.proveedor;
    }

    public String getCorredor(){
        return this.corredor;
    }

    public String getTelefono(){
        return this.telefono;
    }

    public Boolean getActivo(){
        return this.activo;
    }

    public void setProveedor(String proveedor){
        this.proveedor = ValidarString.validarString(proveedor);
    }

    public void setCorredor(String corredor){
        this.corredor = corredor;
    }

    public void setTelefono(String telefono){
        this.telefono = telefono;
    }

    public void activarProveedor(){
        this.activo = true;
    }

    public void desactivarProveedor(){
        this.activo = false;
    }

}
