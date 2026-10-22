package com.spacehub.controller;

import com.spacehub.model.Categoria;
import com.spacehub.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categorias")
public class CategoriaController {
    private final CategoriaService categorias;
    public CategoriaController(CategoriaService categorias) { this.categorias = categorias; }

    @InitBinder("categoria")
    public void configurarFormulario(WebDataBinder binder) {
        binder.setAllowedFields("id", "nombre", "icono", "descripcion");
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categorias.listar());
        return "admin/categorias";
    }
    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "admin/categoria-form";
    }
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", categorias.buscar(id));
        return "admin/categoria-form";
    }
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("categoria") Categoria categoria,
            BindingResult errores, RedirectAttributes flash) {
        if (errores.hasErrors()) return "admin/categoria-form";
        try {
            categorias.guardar(categoria);
        } catch (IllegalArgumentException | DataIntegrityViolationException ex) {
            errores.rejectValue("nombre", "duplicado", "Ya existe una categoría con ese nombre.");
            return "admin/categoria-form";
        }
        flash.addFlashAttribute("mensaje", "Categoría guardada correctamente.");
        return "redirect:/admin/categorias";
    }
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            categorias.eliminar(id);
            flash.addFlashAttribute("mensaje", "Categoría eliminada correctamente.");
        } catch (IllegalArgumentException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            flash.addFlashAttribute("error", "No se puede eliminar la categoría porque tiene espacios asociados.");
        }
        return "redirect:/admin/categorias";
    }
}
