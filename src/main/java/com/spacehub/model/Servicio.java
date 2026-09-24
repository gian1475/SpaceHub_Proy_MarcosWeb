package com.spacehub.model;

import java.util.List;

public class Servicio {
    private Long id;
    private String nombre;
    private Categoria categoria;
    private String distrito;
    private String direccion;
    private String referencia;
    private double precioPorHora;
    private int capacidad;
    private String estado; // "Disponible" / "Ocupado"
    private String descripcionCorta;
    private String descripcion;
    private List<ServicioIncluido> servicios;
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
        this.precioPorHora = precioPorHora;
        this.capacidad = capacidad;
        this.estado = estado;
        this.descripcionCorta = descripcionCorta;
        this.descripcion = descripcion;
        this.servicios = servicios;
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

    public double getPrecioPorHora() {
        return precioPorHora;
    }

    public void setPrecioPorHora(double precioPorHora) {
        this.precioPorHora = precioPorHora;
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
    }

    public List<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<String> imagenes) {
        this.imagenes = imagenes;
    }
}
