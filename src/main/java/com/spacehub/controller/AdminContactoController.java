package com.spacehub.controller;
import com.spacehub.service.ContactoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequestMapping("/admin/mensajes")
public class AdminContactoController {
    private final ContactoService mensajes;
    public AdminContactoController(ContactoService mensajes) { this.mensajes = mensajes; }
    @GetMapping public String listar(@RequestParam(required = false) Boolean atendido,
            @RequestParam(defaultValue = "0") int pagina, Model model) {
        model.addAttribute("pagina", mensajes.listar(atendido, pagina)); model.addAttribute("atendido", atendido);
        return "admin/mensajes";
    }
    @PostMapping("/{id}/estado") public String estado(@PathVariable Long id, @RequestParam boolean atendido, RedirectAttributes flash) {
        mensajes.estado(id, atendido); flash.addFlashAttribute("mensaje", "Estado del mensaje actualizado.");
        return "redirect:/admin/mensajes";
    }
}
