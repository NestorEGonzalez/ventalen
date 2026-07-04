package com.ventalen.motivo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "motivo")
public class Motivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String motivo;

    @Column(nullable = false)
    private Boolean afectaPositivo = false;

    public Motivo(){}

    public Motivo(String motivo, Boolean afectaPositivo){
        this.motivo = motivo;
        this.afectaPositivo = afectaPositivo != null ? afectaPositivo : false;
    }

    public Long getId(){
        return this.id;
    }

    public String getMotivo(){
        return this.motivo;
    }

    public Boolean getAfectaPositivo(){
        return this.afectaPositivo;
    }

    public void setMotivo(String motivo){
        this.motivo = motivo;
    }

    public void setAfectaPositivo(Boolean afectaPositivo){
        this.afectaPositivo = afectaPositivo;
    }

}
