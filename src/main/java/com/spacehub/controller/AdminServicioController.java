package com.spacehub.controller;

import com.spacehub.form.ServicioForm;
import com.spacehub.service.ServicioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;

@Controller
@RequestMapping("/admin/servicios")
public class AdminServicioController {
    private final ServicioService servicios;
    public AdminServicioController(ServicioService servicios) { this.servicios = servicios; }
    @InitBinder("servicioForm")
    public void binder(WebDataBinder binder) {
        binder.setAllowedFields("id", "nombre", "categoriaId", "distrito", "direccion", "referencia", "precioPorHora",
                "capacidad", "estado", "descripcionCorta", "descripcion", "imagenes", "comodidades");
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }
    @GetMapping
    public String listar(Model model) { model.addAttribute("servicios", servicios.listarTodos()); return "admin/servicios"; }
    private String formulario(Model model) {
        model.addAttribute("categorias", servicios.listarCategorias()); return "admin/servicio-form";
    }
    @GetMapping("/nuevo")
    public String nuevo(Model model) { model.addAttribute("servicioForm", new ServicioForm()); return formulario(model); }
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("servicioForm", servicios.formulario(id)); return formulario(model);
    }
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("servicioForm") ServicioForm form, BindingResult errores,
            Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) return formulario(model);
        try { servicios.guardar(form); }
        catch (IllegalArgumentException ex) { errores.reject("datos", ex.getMessage()); return formulario(model); }
        catch (DataIntegrityViolationException ex) {
            errores.reject("integridad", "Los datos relacionados cambiaron. Revise la categoría y vuelva a guardar.");
            return formulario(model);
        }
        flash.addFlashAttribute("mensaje", "Espacio guardado correctamente."); return "redirect:/admin/servicios";
    }
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try { servicios.eliminar(id); flash.addFlashAttribute("mensaje", "Espacio eliminado correctamente."); }
        catch (IllegalArgumentException ex) { flash.addFlashAttribute("error", ex.getMessage()); }
        catch (DataIntegrityViolationException ex) { flash.addFlashAttribute("error", "No se puede eliminar el espacio porque tiene reservas asociadas."); }
        return "redirect:/admin/servicios";
    }
}
