package com.ventalen.ingreso;

import java.math.BigDecimal;

import com.ventalen.producto.Producto;
import com.ventalen.stock.Stock;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalle_ingreso")
public class DetalleIngreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ingreso", nullable = false)
    private Ingreso ingreso;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private BigDecimal precioMayorista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_stock")
    private Stock stock;

    public DetalleIngreso(){}

    public Long getId(){
        return this.id;
    }

    public Ingreso getIngreso(){
        return this.ingreso;
    }

    public void setIngreso(Ingreso ingreso){
        this.ingreso = ingreso;
    }

    public Producto getProducto(){
        return this.producto;
    }

    public void setProducto(Producto producto){
        this.producto = producto;
    }

    public Integer getCantidad(){
        return this.cantidad;
    }

    public void setCantidad(Integer cantidad){
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioMayorista(){
        return this.precioMayorista;
    }

    public void setPrecioMayorista(BigDecimal precioMayorista){
        this.precioMayorista = precioMayorista;
    }

    public Stock getStock(){
        return this.stock;
    }

    public void setStock(Stock stock){
        this.stock = stock;
    }

}
