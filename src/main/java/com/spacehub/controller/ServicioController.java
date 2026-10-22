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

    private final com.spacehub.service.ConsultaEspaciosService consultas;
    public ServicioController(ServicioService servicioService, com.spacehub.service.ConsultaEspaciosService consultas) {
        this.servicioService = servicioService;
        this.consultas = consultas;
    }

    @GetMapping("/servicios")
    public String servicios(@RequestParam(name = "categoria", required = false) String categoria,
            @RequestParam(name = "categoriaId", required = false) Long categoriaId,
            @RequestParam(name = "espacioId", required = false) Long espacioId, Model model) {
        if (categoriaId == null) categoriaId = consultas.categoriaPorNombre(categoria);
        consultas.preparar(categoriaId, espacioId, model);
        model.addAttribute("servicios", consultas.consultar(categoriaId, espacioId));
        return "servicios";
    }

    @GetMapping("/servicios/{id}")
    public String detalle(@PathVariable("id") Long id, Model model) {
        Servicio servicio = servicioService.buscarObligatorio(id);
        model.addAttribute("servicio", servicio);
        model.addAttribute("fechaMinima", java.time.LocalDate.now(java.time.ZoneId.of("America/Lima")));
        return "detalle";
    }
}
