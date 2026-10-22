package com.spacehub.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "servicios")
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nombre;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    @Column(nullable = false, length = 50)
    private String distrito;
    @Column(nullable = false, length = 150)
    private String direccion;
    @Column(length = 150)
    private String referencia;
    @Column(name = "precio_por_hora", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal precioPorHora;
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<ServicioImagen> imagenesPersistidas = new java.util.ArrayList<>();
    public List<ServicioImagen> getImagenesPersistidas() { return imagenesPersistidas; }
    public void setImagenesPersistidas(List<ServicioImagen> imagenesPersistidas) {
        this.imagenesPersistidas.clear();
        if (imagenesPersistidas != null) for (ServicioImagen imagen : imagenesPersistidas) {
            imagen.setServicio(this);
            this.imagenesPersistidas.add(imagen);
        }
    }
    @Column(nullable = false)
    private int capacidad;
    @Column(nullable = false, length = 20)
    private String estado; // Un espacio Ocupado no acepta nuevas solicitudes.
    @Column(name = "descripcion_corta", length = 200)
    private String descripcionCorta;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<ServicioIncluido> servicios = new java.util.ArrayList<>();
    @Transient
    private List<String> imagenes;

    public Servicio() {
    }

    public Servicio(Long id, String nombre, Categoria categoria, String distrito, String direccion, String referencia,
                    double precioPorHora, int capacidad, String estado, String descripcionCorta, String descripcion,
                    List<ServicioIncluido> servicios, List<String> imagenes) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.distrito = distrito;
        this.direccion = direccion;
        this.referencia = referencia;
        this.precioPorHora = java.math.BigDecimal.valueOf(precioPorHora);
        this.capacidad = capacidad;
        this.estado = estado;
        this.descripcionCorta = descripcionCorta;
        this.descripcion = descripcion;
        this.servicios = servicios;
        if (servicios != null) servicios.forEach(incluido -> incluido.setServicio(this));
        this.imagenes = imagenes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public void setPrecioPorHoraDecimal(java.math.BigDecimal precio) { this.precioPorHora = precio; }

    public java.math.BigDecimal getPrecioPorHoraDecimal() { return precioPorHora; }

    public double getPrecioPorHora() {
        return precioPorHora == null ? 0 : precioPorHora.doubleValue();
    }

    public void setPrecioPorHora(double precioPorHora) {
        this.precioPorHora = java.math.BigDecimal.valueOf(precioPorHora);
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcionCorta() {
        return descripcionCorta;
    }

    public void setDescripcionCorta(String descripcionCorta) {
        this.descripcionCorta = descripcionCorta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<ServicioIncluido> getServicios() {
        return servicios;
    }

    public void setServicios(List<ServicioIncluido> servicios) {
        this.servicios = servicios;
        if (servicios != null) servicios.forEach(incluido -> incluido.setServicio(this));
    }

    public List<String> getImagenes() {
        return imagenes != null ? imagenes : imagenesPersistidas.stream().map(ServicioImagen::getUrl).toList();
    }

    public void setImagenes(List<String> imagenes) {
        this.imagenes = imagenes;
    }
}
