package com.spacehub.controller;

import com.spacehub.service.ReservaService;
import com.spacehub.form.ReservaForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.ConcurrencyFailureException;

@Controller
@RequestMapping("/admin/reservas")
public class AdminReservaController {
    private final ReservaService reservas;
    public AdminReservaController(ReservaService reservas) { this.reservas=reservas; }
    @InitBinder("reservaForm")
    public void binder(org.springframework.web.bind.WebDataBinder binder) { binder.setAllowedFields("id","fecha","horaInicio","horaFin","estado"); }
    @GetMapping
    public String listar(Model model) { model.addAttribute("reservas",reservas.listarTodas()); return "admin/reservas"; }
    private String formulario(ReservaForm form, Model model) {
        model.addAttribute("reserva",reservas.buscar(form.getId())); return "admin/reserva-form";
    }
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        var f=reservas.formulario(id); model.addAttribute("reservaForm",f); return formulario(f,model);
    }
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute ReservaForm reservaForm, BindingResult errores, Model model, RedirectAttributes flash) {
        if (reservaForm.getId()==null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST);
        if (errores.hasErrors()) return formulario(reservaForm,model);
        try { reservas.editar(reservaForm); }
        catch (IllegalArgumentException ex) { errores.reject("reserva",ex.getMessage()); return formulario(reservaForm,model); }
        catch (ConcurrencyFailureException ex) { errores.reject("concurrencia","Otra solicitud está modificando este espacio. Intenta de nuevo."); return formulario(reservaForm,model); }
        flash.addFlashAttribute("mensaje","Reserva actualizada correctamente."); return "redirect:/admin/reservas";
    }
    @PostMapping("/estado/{id}")
    public String estado(@PathVariable Long id, @RequestParam String estado, RedirectAttributes flash) {
        try { reservas.cambiarEstado(id,estado); flash.addFlashAttribute("mensaje","Estado actualizado correctamente."); }
        catch (IllegalArgumentException ex) { flash.addFlashAttribute("error",ex.getMessage()); }
        catch (ConcurrencyFailureException ex) { flash.addFlashAttribute("error","Otra solicitud está modificando este espacio. Intenta de nuevo."); }
        return "redirect:/admin/reservas";
    }
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try { reservas.eliminar(id); flash.addFlashAttribute("mensaje","Reserva eliminada correctamente."); }
        catch (ConcurrencyFailureException ex) { flash.addFlashAttribute("error","Otra solicitud está modificando este espacio. Intenta de nuevo."); }
        return "redirect:/admin/reservas";
    }
}
