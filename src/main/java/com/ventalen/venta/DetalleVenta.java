package com.ventalen.venta;

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
@Table(name = "detalle_venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private BigDecimal precioVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_stock")
    private Stock stock;

    public DetalleVenta(){}

    public Long getId(){
        return this.id;
    }

    public Venta getVenta(){
        return this.venta;
    }

    public void setVenta(Venta venta){
        this.venta = venta;
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

    public BigDecimal getPrecioVenta(){
        return this.precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta){
        this.precioVenta = precioVenta;
    }

    public Stock getStock(){
        return this.stock;
    }

    public void setStock(Stock stock){
        this.stock = stock;
    }

}
