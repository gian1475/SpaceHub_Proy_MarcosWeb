package com.spacehub.controller;
import com.spacehub.service.AdminUsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequestMapping("/admin/usuarios")
public class AdminUsuarioController {
    private final AdminUsuarioService usuarios;
    public AdminUsuarioController(AdminUsuarioService usuarios) { this.usuarios = usuarios; }
    @GetMapping public String listar(@RequestParam(defaultValue="0") int pagina, Model model) {
        model.addAttribute("pagina",usuarios.listar(pagina)); return "admin/usuarios";
    }
    @PostMapping("/{id}/guardar") public String guardar(@PathVariable Long id, @RequestParam String rol,
            @RequestParam(defaultValue="false") boolean activo, jakarta.servlet.http.HttpSession session, RedirectAttributes flash) {
        try { usuarios.actualizar(id,rol,activo,(Long)session.getAttribute("usuarioId")); flash.addFlashAttribute("mensaje","Usuario actualizado. Los cambios de rol o estado revocan sus tokens anteriores."); }
        catch (IllegalArgumentException ex) { flash.addFlashAttribute("error",ex.getMessage()); }
        catch (org.springframework.dao.ConcurrencyFailureException ex) { flash.addFlashAttribute("error","Otra operación está modificando usuarios. Intenta de nuevo."); }
        return "redirect:/admin/usuarios";
    }
}
