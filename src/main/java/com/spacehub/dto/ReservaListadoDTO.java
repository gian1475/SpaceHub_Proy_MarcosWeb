package com.spacehub.dto;

/** Reúne los datos de la tabla de reservas para que la vista no dependa de las entidades. */
public class ReservaListadoDTO {
    private final Long id;
    private final String clienteNombre;
    private final String clienteCorreo;
    private final String espacioNombre;
    private final String categoriaNombre;
    private final java.time.LocalDate fecha;
    private final java.time.LocalTime horaInicio;
    private final java.time.LocalTime horaFin;
    private final java.math.BigDecimal total;
    private final String estado;
    public ReservaListadoDTO(Long id, String clienteNombre, String clienteCorreo, String espacioNombre, String categoriaNombre, java.time.LocalDate fecha, java.time.LocalTime horaInicio, java.time.LocalTime horaFin, java.math.BigDecimal total, String estado) {
        this.id = id;
        this.clienteNombre = clienteNombre;
        this.clienteCorreo = clienteCorreo;
        this.espacioNombre = espacioNombre;
        this.categoriaNombre = categoriaNombre;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.total = total;
        this.estado = estado;
    }
    public Long getId() { return id; }
    public String getClienteNombre() { return clienteNombre; }
    public String getClienteCorreo() { return clienteCorreo; }
    public String getEspacioNombre() { return espacioNombre; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public java.time.LocalDate getFecha() { return fecha; }
    public java.time.LocalTime getHoraInicio() { return horaInicio; }
    public java.time.LocalTime getHoraFin() { return horaFin; }
    public java.math.BigDecimal getTotal() { return total; }
    public String getEstado() { return estado; }
    public String getDuracionTexto() {
        long minutos = java.time.Duration.between(horaInicio, horaFin).toMinutes();
        return (minutos / 60) + " h" + (minutos % 60 == 0 ? "" : " " + minutos % 60 + " min");
    }
}
