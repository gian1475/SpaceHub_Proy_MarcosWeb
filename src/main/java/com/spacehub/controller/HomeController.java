package com.spacehub.controller;

import com.spacehub.service.ServicioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ServicioService servicioService;

    public HomeController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("categorias", servicioService.listarCategorias());
        model.addAttribute("destacados", servicioService.obtenerDestacados());
        return "index";
    }
}
