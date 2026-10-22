package com.spacehub.form;

import jakarta.validation.constraints.*;

public class ServicioForm {
    
    private Long id;
    @NotBlank(message="El nombre es obligatorio.") @Size(min=3,max=100,message="Use entre 3 y 100 caracteres.")
    private String nombre;
    @NotNull(message="Seleccione una categoría.")
    private Long categoriaId;
    @NotBlank(message="El distrito es obligatorio.") @Size(max=50)
    private String distrito;
    @NotBlank(message="La dirección es obligatoria.") @Size(max=150)
    private String direccion;
    @Size(max=150)
    private String referencia;
    @NotNull @DecimalMin(value="1.00",message="El precio mínimo es S/ 1.00.") @Digits(integer=8,fraction=2)
    private java.math.BigDecimal precioPorHora;
    @NotNull @Min(1) @Max(1000)
    private Integer capacidad;
    @NotBlank @Pattern(regexp="Disponible|Ocupado",message="Seleccione un estado válido.")
    private String estado = "Disponible";
    @Size(max=200)
    private String descripcionCorta;
    @Size(max=10000)
    private String descripcion;
    @Size(max=20000)
    private String imagenes;
    @Size(max=10000)
    private String comodidades;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public java.math.BigDecimal getPrecioPorHora() { return precioPorHora; }
    public void setPrecioPorHora(java.math.BigDecimal precioPorHora) { this.precioPorHora = precioPorHora; }
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getDescripcionCorta() { return descripcionCorta; }
    public void setDescripcionCorta(String descripcionCorta) { this.descripcionCorta = descripcionCorta; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getImagenes() { return imagenes; }
    public void setImagenes(String imagenes) { this.imagenes = imagenes; }
    public String getComodidades() { return comodidades; }
    public void setComodidades(String comodidades) { this.comodidades = comodidades; }
    @AssertTrue(message="Cada imagen debe ser una URL http o https de hasta 255 caracteres, una por línea (máximo 20).")
    public boolean isImagenesValidas() {
        var lineas = lineas(imagenes);
        return lineas.size() <= 20 && lineas.stream().allMatch(url -> {
            try {
                var uri = java.net.URI.create(url);
                return url.length() <= 255 && ("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))
                        && uri.getHost() != null;
            } catch (IllegalArgumentException ex) { return false; }
        });
    }
    @AssertTrue(message="Use icono|texto para cada comodidad, por ejemplo bi-wifi|WiFi. Máximo 30 comodidades; icono hasta 50 y texto hasta 100 caracteres.")
    public boolean isComodidadesValidas() {
        var lineas = lineas(comodidades);
        return lineas.size() <= 30 && lineas.stream().allMatch(linea -> {
            var partes = linea.split("\\|", -1);
            return partes.length == 2 && partes[0].trim().length() <= 50
                    && partes[0].trim().matches("bi-[a-z0-9-]+")
                    && !partes[1].isBlank() && partes[1].trim().length() <= 100;
        });
    }
    public static java.util.List<String> lineas(String texto) {
        return texto == null ? java.util.List.of() : texto.lines().map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
}
