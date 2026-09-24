package com.spacehub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactoController {

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }

    @PostMapping("/contacto")
    public String procesarContacto(
            @RequestParam("nombre") String nombre,
            @RequestParam("correo") String correo,
            @RequestParam("motivo") String motivo,
            @RequestParam("mensaje") String mensaje,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("mensaje", "¡Mensaje enviado con éxito!");
        return "redirect:/contacto";
    }
}
