package com.spacehub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "servicio_incluidos")
public class ServicioIncluido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }

    @Column(length = 50)
    private String icono;
    @Column(nullable = false, length = 100)
    private String texto;

    public ServicioIncluido() {
    }

    public ServicioIncluido(String icono, String texto) {
        this.icono = icono;
        this.texto = texto;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
