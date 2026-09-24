package com.spacehub.controller;

import com.spacehub.model.Rol;
import com.spacehub.model.Usuario;
import com.spacehub.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam("correo") String correo,
            @RequestParam("password") String password,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOpt = usuarioService.validarLogin(correo, password);

        if (usuarioOpt.isPresent()) {
            Usuario u = usuarioOpt.get();
            if (u.getRol() == Rol.ADMIN) {
                redirectAttributes.addFlashAttribute("mensaje", "¡Bienvenido al Panel de Administración!");
                return "redirect:/administracion";
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "¡Inicio de sesión exitoso! Bienvenido a SpaceHub.");
                return "redirect:/";
            }
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos.");
            return "login";
        }
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @RequestParam("nombres") String nombres,
            @RequestParam("apellidos") String apellidos,
            @RequestParam("correo") String correo,
            @RequestParam("telefono") String telefono,
            @RequestParam(name = "tipo", defaultValue = "cliente") String tipo,
            @RequestParam("password") String password,
            Model model,
            RedirectAttributes redirectAttributes) {

        boolean exito = usuarioService.registrarUsuario(nombres, apellidos, correo, telefono, tipo, password);
        if (exito) {
            redirectAttributes.addFlashAttribute("mensaje", "¡Cuenta registrada exitosamente!");
            return "redirect:/login";
        } else {
            model.addAttribute("error", "El correo electrónico ya se encuentra registrado.");
            return "registro";
        }
    }
}
