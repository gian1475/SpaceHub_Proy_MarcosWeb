package com.spacehub.controller;

import com.spacehub.model.Servicio;
import com.spacehub.service.ServicioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping("/servicios")
    public String servicios(@RequestParam(name = "categoria", required = false) String categoria, Model model) {
        model.addAttribute("servicios", servicioService.filtrarPorCategoria(categoria));
        model.addAttribute("categorias", servicioService.listarCategorias());
        model.addAttribute("categoriaActiva", categoria != null ? categoria : "Todos");
        return "servicios";
    }

    @GetMapping("/servicios/{id}")
    public String detalle(@PathVariable("id") Long id, Model model) {
        Servicio servicio = servicioService.buscarPorId(id);
        if (servicio == null) {
            return "redirect:/servicios";
        }
        model.addAttribute("servicio", servicio);
        return "detalle";
    }
}
