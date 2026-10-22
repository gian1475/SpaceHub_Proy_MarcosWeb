package com.spacehub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "servicio_imagenes")
public class ServicioImagen {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;
    @Column(nullable = false, length = 255)
    private String url;
    public ServicioImagen() { }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
