package com.ventalen.producto;

import java.math.BigDecimal;

import com.ventalen.funciones.ValidarString;
import com.ventalen.funciones.ValidarPrecio;
import com.ventalen.categoria.Categoria;
import com.ventalen.exception.ErrorCampoVacioONulo;
import com.ventalen.exception.ErrorPrecioInvalido;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

@Entity

@Table(name="producto")

public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO)
    @Column(unique = true, nullable = false)
    private String nombre;

    @PositiveOrZero(message = ErrorPrecioInvalido.ERROR_PRECIO_NEGATIVO)
    private BigDecimal precio;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    private boolean activo = true;

    public Producto(){}

    public Producto(String nombre, BigDecimal precio, Categoria categoria){
        this.nombre = ValidarString.validarString(nombre);
        this.precio = ValidarPrecio.validarPrecio(precio);
        this.categoria = categoria;
    }

    public String getNombre(){
        return this.nombre;
    }

    public BigDecimal getPrecio(){
        return this.precio;
    }

    public Categoria getCategoria(){
        return this.categoria != null ? this.categoria: null;
    }


    public void setNombre(String nombre){
        this.nombre = ValidarString.validarString(nombre);

    }

    public void setPrecio(BigDecimal precio){
        this.precio = ValidarPrecio.validarPrecio(precio);
    }


    public void setCategoria(Categoria categoria){
        this.categoria = categoria;
    }

    public Long getId() {
        return this.id;
    }

    public void activarProducto(){
        this.activo = true;
    }

    public void desactivarProducto(){
        this.activo = false;
    }

}
