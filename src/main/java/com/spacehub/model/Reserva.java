package com.spacehub.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;
    @Transient
    private String clienteNombre;
    @Transient
    private String clienteCorreo;
    @Column(nullable = false)
    private LocalDate fecha;
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
    @Column(nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal total;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    @Column(nullable = false, length = 20)
    private String estado; // Solo Pendiente y Aprobada bloquean el horario del espacio.

    public Reserva() {
    }

    public Reserva(Long id, Servicio servicio, String clienteNombre, String clienteCorreo, LocalDate fecha,
                   LocalTime horaInicio, LocalTime horaFin, double total, String estado) {
        this.id = id;
        this.servicio = servicio;
        this.clienteNombre = clienteNombre;
        this.clienteCorreo = clienteCorreo;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.total = java.math.BigDecimal.valueOf(total);
        this.estado = estado;
    }

    public double getHoras() {
        return horaInicio == null || horaFin == null ? 0 : java.time.Duration.between(horaInicio, horaFin).toSeconds() / 3600.0;
    }
    public String getDuracionTexto() {
        long minutos = horaInicio == null || horaFin == null ? 0 : java.time.Duration.between(horaInicio, horaFin).toMinutes();
        return (minutos / 60) + " h" + (minutos % 60 == 0 ? "" : " " + (minutos % 60) + " min");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public String getClienteNombre() {
        return usuario != null ? usuario.getNombres() + " " + usuario.getApellidos() : clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteCorreo() {
        return usuario != null ? usuario.getCorreo() : clienteCorreo;
    }

    public void setClienteCorreo(String clienteCorreo) {
        this.clienteCorreo = clienteCorreo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public java.math.BigDecimal getTotalDecimal() { return total; }
    public void setTotalDecimal(java.math.BigDecimal total) { this.total = total; }

    public double getTotal() {
        return total == null ? 0 : total.doubleValue();
    }

    public void setTotal(double total) {
        this.total = java.math.BigDecimal.valueOf(total);
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
