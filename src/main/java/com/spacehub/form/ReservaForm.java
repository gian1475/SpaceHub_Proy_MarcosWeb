package com.spacehub.form;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.*;

public class ReservaForm {
    private Long id;
    @NotNull(message="Seleccione una fecha.")
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate fecha;
    @NotNull(message="Seleccione la hora inicial.")
    @DateTimeFormat(iso=DateTimeFormat.ISO.TIME)
    private LocalTime horaInicio;
    @NotNull(message="Seleccione la hora final.")
    @DateTimeFormat(iso=DateTimeFormat.ISO.TIME)
    private LocalTime horaFin;
    @NotBlank @Pattern(regexp="Pendiente|Aprobada|Rechazada",message="Seleccione un estado válido.")
    private String estado = "Pendiente";
    public Long getId() { return id; }
    public void setId(Long id) { this.id=id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha=fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio=horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin=horaFin; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado=estado; }
    @AssertTrue(message="La reserva debe durar al menos una hora.")
    public boolean isDuracionValida() {
        return horaInicio == null || horaFin == null || !horaFin.isAfter(horaInicio)
                || Duration.between(horaInicio, horaFin).compareTo(Duration.ofHours(1)) >= 0;
    }

    @AssertTrue(message="La hora final debe ser posterior a la inicial.")
    public boolean isHorarioValido() { return horaInicio==null || horaFin==null || horaFin.isAfter(horaInicio); }
}
