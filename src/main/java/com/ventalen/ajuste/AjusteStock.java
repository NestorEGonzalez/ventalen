package com.ventalen.ajuste;

import java.time.LocalDate;

import com.ventalen.motivo.Motivo;
import com.ventalen.producto.Producto;
import com.ventalen.stock.Stock;
import com.ventalen.auth.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "ajuste_stock")
public class AjusteStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha = LocalDate.now();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer cantidad;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_motivo", nullable = false)
    private Motivo motivo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    private String detalles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_stock")
    private Stock stock;

    public AjusteStock(){}

    public Long getId(){
        return this.id;
    }

    public LocalDate getFecha(){
        return this.fecha;
    }

    public Producto getProducto(){
        return this.producto;
    }

    public Integer getCantidad(){
        return this.cantidad;
    }

    public Motivo getMotivo(){
        return this.motivo;
    }

    public Usuario getUsuario(){
        return this.usuario;
    }

    public String getDetalles(){
        return this.detalles;
    }

    public Stock getStock(){
        return this.stock;
    }

    public void setProducto(Producto producto){
        this.producto = producto;
    }

    public void setCantidad(Integer cantidad){
        this.cantidad = cantidad;
    }

    public void setMotivo(Motivo motivo){
        this.motivo = motivo;
    }

    public void setUsuario(Usuario usuario){
        this.usuario = usuario;
    }

    public void setDetalles(String detalles){
        this.detalles = detalles;
    }

    public void setStock(Stock stock){
        this.stock = stock;
    }

}
