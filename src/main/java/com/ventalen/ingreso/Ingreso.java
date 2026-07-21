package com.ventalen.ingreso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ventalen.proveedor.Proveedor;
import com.ventalen.auth.Usuario;
import com.ventalen.detalleIngreso.DetalleIngreso;

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
@Table(name = "ingreso")
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha = LocalDate.now();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @OneToMany(mappedBy = "ingreso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleIngreso> detalles = new ArrayList<>();

    public Ingreso(){}

    public Ingreso(Usuario usuario, Proveedor proveedor){
        this.usuario = usuario;
        this.proveedor = proveedor;
    }

    public Long getId(){
        return this.id;
    }

    public LocalDate getFecha(){
        return this.fecha;
    }

    public Usuario getUsuario(){
        return this.usuario;
    }

    public Proveedor getProveedor(){
        return this.proveedor;
    }

    public List<DetalleIngreso> getDetalles(){
        return this.detalles;
    }

    public void addDetalle(DetalleIngreso detalle){
        detalles.add(detalle);
        detalle.setIngreso(this);
    }

    public void removeDetalle(DetalleIngreso detalle){
        detalles.remove(detalle);
        detalle.setIngreso(null);
    }

}
