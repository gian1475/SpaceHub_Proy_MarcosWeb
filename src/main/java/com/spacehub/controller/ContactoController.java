package com.spacehub.controller;

import com.spacehub.form.ContactoForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactoController {
    @InitBinder("contactoForm")
    public void binder(org.springframework.web.bind.WebDataBinder binder) {
        binder.setAllowedFields("nombre","correo","motivo","mensaje");
        binder.registerCustomEditor(String.class,new org.springframework.beans.propertyeditors.StringTrimmerEditor(false));
    }
    @GetMapping("/contacto")
    public String contacto(Model model) { model.addAttribute("contactoForm",new ContactoForm()); return "contacto"; }
    @PostMapping("/contacto")
    public String procesarContacto(@Valid @ModelAttribute ContactoForm contactoForm, BindingResult errores, RedirectAttributes flash) {
        if (errores.hasErrors()) return "contacto";
        flash.addFlashAttribute("mensaje","Datos validados. Este formulario de demostración no envía ni guarda mensajes.");
        return "redirect:/contacto";
    }
}
