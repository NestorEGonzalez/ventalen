package com.ventalen.venta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ventalen.auth.Usuario;
import com.ventalen.detalleVenta.DetalleVenta;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha = LocalDate.now();

    @Column(nullable = false)
    private Boolean pagado = false;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta(){}

    public Venta(Usuario usuario, Boolean pagado){
        this.usuario = usuario;
        this.pagado = pagado != null ? pagado : false;
    }

    public Long getId(){
        return this.id;
    }

    public LocalDate getFecha(){
        return this.fecha;
    }

    public Boolean getPagado(){
        return this.pagado;
    }

    public Usuario getUsuario(){
        return this.usuario;
    }

    public List<DetalleVenta> getDetalles(){
        return this.detalles;
    }

    public void addDetalle(DetalleVenta detalle){
        detalles.add(detalle);
        detalle.setVenta(this);
    }

    public void removeDetalle(DetalleVenta detalle){
        detalles.remove(detalle);
        detalle.setVenta(null);
    }

}
