package com.spacehub.controller;

import com.spacehub.service.ReservaService;
import com.spacehub.service.ServicioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final ServicioService servicioService;
    private final ReservaService reservaService;

    public AdminController(ServicioService servicioService, ReservaService reservaService) {
        this.servicioService = servicioService;
        this.reservaService = reservaService;
    }

    @GetMapping("/administracion")
    public String administracion(Model model) {
        model.addAttribute("servicios", servicioService.listarTodos());
        model.addAttribute("reservas", reservaService.listarTodas());
        model.addAttribute("totalEspacios", reservaService.obtenerTotalEspacios());
        model.addAttribute("totalReservas", reservaService.obtenerTotalReservas());
        model.addAttribute("pendientes", reservaService.obtenerReservasPendientesCount());
        model.addAttribute("ingresos", reservaService.calcularIngresosTotales());
        return "administracion";
    }
}
