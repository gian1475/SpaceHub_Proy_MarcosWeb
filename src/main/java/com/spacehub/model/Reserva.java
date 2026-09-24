package com.spacehub.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {
    private Long id;
    private Servicio servicio;
    private String clienteNombre;
    private String clienteCorreo;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private double total;
    private String estado; // "Pendiente" / "Aprobada" / "Rechazada"

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
        this.total = total;
        this.estado = estado;
    }

    public int getHoras() {
        if (horaInicio != null && horaFin != null) {
            int diff = horaFin.getHour() - horaInicio.getHour();
            return diff > 0 ? diff : 1;
        }
        return 1;
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
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteCorreo() {
        return clienteCorreo;
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

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
