package com.spacehub.service;

import com.spacehub.model.Reserva;
import com.spacehub.model.Servicio;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservaService {

    private final ServicioService servicioService;
    private final List<Reserva> reservas = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(101);

    public ReservaService(ServicioService servicioService) {
        this.servicioService = servicioService;
        cargarReservasSemilla();
    }

    private void cargarReservasSemilla() {
        Servicio s2 = servicioService.buscarPorId(2L); // Rooftop Sunset Miraflores (190.00)
        Servicio s1 = servicioService.buscarPorId(1L); // Coworking Loft San Isidro (45.00)
        Servicio s4 = servicioService.buscarPorId(4L); // Studio Click Barranco (80.00)

        // RES-101
        reservas.add(new Reserva(
                contadorId.getAndIncrement(),
                s2,
                "Gerardo Guevara",
                "gerardo@correo.com",
                LocalDate.of(2026, 9, 12),
                LocalTime.of(14, 0),
                LocalTime.of(18, 0),
                760.00,
                "Aprobada"));

        // RES-102
        reservas.add(new Reserva(
                contadorId.getAndIncrement(),
                s1,
                "Juan De Dios Chiu",
                "juan@correo.com",
                LocalDate.of(2026, 9, 15),
                LocalTime.of(9, 0),
                LocalTime.of(14, 0),
                225.00,
                "Pendiente"));

        // RES-103
        reservas.add(new Reserva(
                contadorId.getAndIncrement(),
                s4,
                "Jahyre Turin",
                "jahyre@correo.com",
                LocalDate.of(2026, 9, 18),
                LocalTime.of(10, 0),
                LocalTime.of(16, 0),
                480.00,
                "Pendiente"));
    }

    public List<Reserva> listarTodas() {
        List<Reserva> copia = new ArrayList<>(reservas);
        Collections.reverse(copia); // Más recientes primero
        return copia;
    }

    public Reserva crearReserva(Long servicioId, String clienteNombre, String clienteCorreo,
            LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        Servicio servicio = servicioService.buscarPorId(servicioId);
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio especificado no existe.");
        }

        int horas = 1;
        if (horaInicio != null && horaFin != null) {
            int diff = horaFin.getHour() - horaInicio.getHour();
            horas = diff > 0 ? diff : 1;
        }

        double total = horas * servicio.getPrecioPorHora();

        Reserva nuevaReserva = new Reserva(
                contadorId.getAndIncrement(),
                servicio,
                clienteNombre,
                clienteCorreo,
                fecha,
                horaInicio,
                horaFin,
                total,
                "Pendiente");

        reservas.add(nuevaReserva);
        return nuevaReserva;
    }

    public int obtenerTotalEspacios() {
        return servicioService.listarTodos().size();
    }

    public int obtenerTotalReservas() {
        return reservas.size();
    }

    public long obtenerReservasPendientesCount() {
        return reservas.stream()
                .filter(r -> "Pendiente".equalsIgnoreCase(r.getEstado()))
                .count();
    }

    public double calcularIngresosTotales() {
        return reservas.stream()
                .filter(r -> !"Rechazada".equalsIgnoreCase(r.getEstado()))
                .mapToDouble(Reserva::getTotal)
                .sum();
    }
}
