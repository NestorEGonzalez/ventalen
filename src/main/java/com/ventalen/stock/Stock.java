package com.ventalen.stock;

import com.ventalen.producto.Producto;
import com.ventalen.exception.ErrorCampoVacioONulo;
import com.ventalen.exception.ErrorCantidadInvalida;
import com.ventalen.funciones.ValidarCantidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "stock")

public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_producto", nullable = false, unique = true)
    private Producto producto;

    @NotNull
    @PositiveOrZero(message = ErrorCantidadInvalida.ERROR_CANTIDAD_NEGATIVA)
    @Column(nullable = false)
    private Integer cantidad = 0;

    public Stock(){}

    public Stock(Producto producto, Integer cantidad){
        validarProducto(producto);
        ValidarCantidad.validarCantidad(cantidad);
        this.producto = producto;
        this.cantidad = cantidad;
    }

    private void validarProducto(Producto producto){
        if (producto == null) {
            throw new ErrorCampoVacioONulo(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO);
        }
    }

    /*private void validarCantidad(Integer cantidad){
        if (cantidad == null) {
            throw new ErrorCantidadInvalida(ErrorCantidadInvalida.ERROR_CANTIDAD_NULA);
        }
        if (cantidad < 0) {
            throw new ErrorCantidadInvalida(ErrorCantidadInvalida.ERROR_CANTIDAD_NEGATIVA);
        }
    }*/

    public Long getId(){
        return this.id;
    }

    public Producto getProducto(){
        return this.producto;
    }

    public Integer getCantidad(){
        return this.cantidad;
    }

    public void setCantidad(Integer cantidad){
        ValidarCantidad.validarCantidad(cantidad);
        this.cantidad = cantidad;
    }

    public void setProducto(Producto producto){
        validarProducto(producto);
        this.producto = producto;
    }

}
