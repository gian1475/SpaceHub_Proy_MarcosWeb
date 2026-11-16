package com.spacehub.controller;

import com.spacehub.service.ServicioService;
import com.spacehub.form.ServicioForm;
import com.spacehub.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequestMapping("/propietario")
public class PropietarioController {
    private final ServicioService espacios;
    private final UsuarioRepository usuarios;
    private final com.spacehub.service.ReservaService reservas;
    public PropietarioController(ServicioService espacios, UsuarioRepository usuarios, com.spacehub.service.ReservaService reservas) {
        this.espacios = espacios; this.usuarios = usuarios; this.reservas = reservas;
    }
    @InitBinder("servicioForm") public void binder(org.springframework.web.bind.WebDataBinder binder) {
        binder.setAllowedFields("id", "nombre", "categoriaId", "distrito", "direccion", "referencia", "precioPorHora", "capacidad", "estado", "descripcionCorta", "descripcion", "imagenes", "comodidades");
        binder.registerCustomEditor(String.class, new org.springframework.beans.propertyeditors.StringTrimmerEditor(false));
    }
    private Long id(HttpSession session) { return (Long) session.getAttribute("usuarioId"); }
    @ModelAttribute public void prepararVista(Model model) {
        model.addAttribute("baseEspacios", "/propietario/espacios");
        model.addAttribute("volverEspacios", "/propietario");
        model.addAttribute("panelEspacios", "/propietario/reservas");
        model.addAttribute("vistaPropietario", true);
    }
    @GetMapping public String listar(Model model, HttpSession session) {
        model.addAttribute("servicios", espacios.listarPropios(id(session))); return "espacios-gestion";
    }
    private String formulario(Model model) { model.addAttribute("categorias", espacios.listarCategorias()); return "espacio-form"; }
    @GetMapping("/espacios/nuevo") public String nuevo(Model model) {
        model.addAttribute("servicioForm", new ServicioForm()); return formulario(model);
    }
    @GetMapping("/espacios/editar/{id}") public String editar(@PathVariable Long id, HttpSession session, Model model) {
        espacios.exigirPropietario(id, id(session)); model.addAttribute("servicioForm", espacios.formulario(id)); return formulario(model);
    }
    @PostMapping("/espacios/guardar") public String guardar(@Valid @ModelAttribute ServicioForm servicioForm, BindingResult errores,
            HttpSession session, Model model, RedirectAttributes flash) {
        if (servicioForm.getId() != null) espacios.exigirPropietario(servicioForm.getId(), id(session));
        if (errores.hasErrors()) return formulario(model);
        try { espacios.guardarPropio(servicioForm, usuarios.findOneById(id(session)).orElseThrow()); }
        catch (IllegalArgumentException | org.springframework.dao.DataIntegrityViolationException ex) {
            errores.reject("datos", "No se pudo guardar. Revisa la categoría, imágenes y comodidades."); return formulario(model);
        }
        flash.addFlashAttribute("mensaje", "Tu espacio se guardó correctamente."); return "redirect:/propietario";
    }
    @PostMapping("/espacios/eliminar/{id}") public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes flash) {
        try { espacios.eliminarPropio(id, id(session)); flash.addFlashAttribute("mensaje", "Espacio eliminado."); }
        catch (IllegalArgumentException | org.springframework.dao.DataIntegrityViolationException ex) {
            flash.addFlashAttribute("error", "No se puede eliminar un espacio con reservas asociadas.");
        }
        return "redirect:/propietario";
    }
    @GetMapping("/reservas") public String reservas(HttpSession session, Model model) {
        model.addAttribute("reservas", reservas.listarDePropietario(id(session))); return "propietario/reservas";
    }
    @PostMapping("/reservas/{id}/estado") public String estado(@PathVariable Long id, @RequestParam String estado, HttpSession session, RedirectAttributes flash) {
        try { reservas.cambiarEstadoPropio(id, estado, id(session)); flash.addFlashAttribute("mensaje", "Solicitud actualizada."); }
        catch (IllegalArgumentException | org.springframework.dao.ConcurrencyFailureException ex) { flash.addFlashAttribute("error", "No se pudo cambiar el estado. Comprueba el horario y la disponibilidad del espacio."); }
        return "redirect:/propietario/reservas";
    }
}
