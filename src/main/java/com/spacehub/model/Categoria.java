package com.spacehub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "categorias")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres.")
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;
    @Size(max = 50, message = "El icono admite hasta 50 caracteres.")
    @Pattern(regexp = "(^$|bi-[a-z0-9-]+)", message = "Use una clase de Bootstrap Icons, por ejemplo bi-building.")
    @Column(length = 50)
    private String icono;
    @Size(max = 200, message = "La descripción admite hasta 200 caracteres.")
    @Column(length = 200)
    private String descripcion;

    public Categoria() {
    }

    public Categoria(Long id, String nombre, String icono, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.icono = icono;
        this.descripcion = descripcion;
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

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
