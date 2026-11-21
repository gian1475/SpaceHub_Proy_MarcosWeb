package com.spacehub.controller;

import com.spacehub.service.ReservaService;
import com.spacehub.model.Reserva;
import com.spacehub.form.SolicitudReservaForm;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.*;
import java.time.*;
import java.math.BigDecimal;

@RestController
@RequestMapping({"/api/reservas", "/api/v1/reservas"})
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public class ReservaApiController {
    private final ReservaService reservas;
    public ReservaApiController(ReservaService reservas) { this.reservas = reservas; }
    public record ReservaDTO(Long id, Long espacioId, String espacio, LocalDate fecha, LocalTime horaInicio,
            LocalTime horaFin, BigDecimal total, String estado) {
        static ReservaDTO desde(Reserva r) { return new ReservaDTO(r.getId(),r.getServicio().getId(),r.getServicio().getNombre(),r.getFecha(),r.getHoraInicio(),r.getHoraFin(),r.getTotalDecimal(),r.getEstado()); }
    }
    @GetMapping public java.util.List<ReservaDTO> propias(Authentication acceso) {
        return reservas.listarPorUsuario(Long.valueOf(acceso.getName())).stream().map(ReservaDTO::desde).toList();
    }
    @PostMapping
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<ReservaDTO> crear(@Valid @RequestBody SolicitudReservaForm entrada, Authentication acceso) {
        var reserva = reservas.crearReserva(entrada.getServicioId(), Long.valueOf(acceso.getName()), entrada.getFecha(), entrada.getHoraInicio(), entrada.getHoraFin());
        return ResponseEntity.status(201).body(ReservaDTO.desde(reserva));
    }
}
