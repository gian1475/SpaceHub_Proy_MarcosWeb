package com.spacehub.controller;

import com.spacehub.model.Reserva;
import com.spacehub.service.ReservaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Controller
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping("/reservas")
    public String reservas(Model model) {
        model.addAttribute("reservas", reservaService.listarTodas());
        return "reservas";
    }

    @PostMapping("/reservas")
    public String crearReserva(
            @RequestParam("servicioId") Long servicioId,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("horaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
            @RequestParam("horaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin,
            @RequestParam("clienteNombre") String clienteNombre,
            @RequestParam("clienteCorreo") String clienteCorreo,
            RedirectAttributes redirectAttributes) {

        Reserva nueva = reservaService.crearReserva(servicioId, clienteNombre, clienteCorreo, fecha, horaInicio, horaFin);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaFormateada = fecha != null ? fecha.format(formatter) : "";
        String totalFormateado = String.format(Locale.US, "%.2f", nueva.getTotal());

        String mensaje = String.format("¡Gracias %s! Tu solicitud de reserva para el %s por un total estimado de S/ %s fue enviada exitosamente.",
                clienteNombre, fechaFormateada, totalFormateado);

        redirectAttributes.addFlashAttribute("mensaje", mensaje);
        return "redirect:/reservas";
    }
}
