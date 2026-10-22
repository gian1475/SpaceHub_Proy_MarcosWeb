package com.spacehub.controller;

import com.spacehub.service.UsuarioService;
import com.spacehub.form.RegistroForm;
import com.spacehub.config.SeguridadWeb;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final UsuarioService usuarios;
    public AuthController(UsuarioService usuarios) { this.usuarios = usuarios; }
    @InitBinder("registroForm")
    public void binder(WebDataBinder binder) {
        binder.setAllowedFields("nombres", "apellidos", "correo", "telefono", "tipo", "password", "terminos");
        for (String campo : java.util.List.of("nombres", "apellidos", "correo", "telefono", "tipo"))
            binder.registerCustomEditor(String.class, campo, new StringTrimmerEditor(false));
    }
    @GetMapping("/login")
    public String login() { return "login"; }
    @PostMapping("/login")
    public String entrar(@RequestParam String correo, @RequestParam String password,
            HttpServletRequest request, Model model, RedirectAttributes flash) {
        if (correo.length() > 100 || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72 || correo.isBlank() || password.isEmpty()) {
            model.addAttribute("error", "Correo o contraseña incorrectos."); return "login";
        }
        var resultado = usuarios.validarLogin(correo, password);
        if (resultado.isEmpty()) {
            model.addAttribute("error", "Correo o contraseña incorrectos."); return "login";
        }
        var anterior = request.getSession(false);
        if (anterior != null) anterior.invalidate();
        var session = request.getSession(true);
        var u = resultado.get();
        session.setAttribute("usuarioId", u.getId());
        session.setAttribute("usuarioNombre", u.getNombres());
        session.setAttribute("usuarioRol", u.getRol().getNombre());
        SeguridadWeb.token(session);
        flash.addFlashAttribute("mensaje", "¡Bienvenido, " + u.getNombres() + "!");
        return "ADMIN".equals(u.getRol().getNombre()) ? "redirect:/administracion" : "redirect:/";
    }
    @PostMapping("/logout")
    public String salir(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session != null) session.invalidate();
        return "redirect:/login";
    }
    @GetMapping("/registro")
    public String registro(Model model) { model.addAttribute("registroForm", new RegistroForm()); return "registro"; }
    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute RegistroForm registroForm, BindingResult errores, RedirectAttributes flash) {
        if (errores.hasErrors()) return "registro";
        boolean exito;
        try {
            exito = usuarios.registrarUsuario(registroForm.getNombres(), registroForm.getApellidos(), registroForm.getCorreo(),
                    registroForm.getTelefono(), registroForm.getTipo(), registroForm.getPassword());
        } catch (DataIntegrityViolationException ex) { exito = false; }
        if (!exito) {
            errores.rejectValue("correo", "duplicado", "El correo ya se encuentra registrado."); return "registro";
        }
        flash.addFlashAttribute("mensaje", "Cuenta registrada correctamente. Inicia sesión.");
        return "redirect:/login";
    }
}
