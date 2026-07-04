package com.ventalen.producto;

import java.math.BigDecimal;

public class ProductoResponse {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Long categoriaId;

    public ProductoResponse(){}
    public ProductoResponse(Long id, String nombre, BigDecimal precio, Long categoriaId){
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.categoriaId = categoriaId;
    }

    public Long getId(){
        return this.id;
    }

    public String getNombre(){
        return this.nombre;
    }

    public BigDecimal getPrecio(){
        return this.precio;
    }

    public Long getCategoriaId(){
        return this.categoriaId;
    }

    public void setId(Long id){
        this.id = id;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public void setPrecio(BigDecimal precio){
        this.precio = precio ;
    }

    public void setCategoriaId(Long categoriaId){
        this.categoriaId = categoriaId ;
    }

}
