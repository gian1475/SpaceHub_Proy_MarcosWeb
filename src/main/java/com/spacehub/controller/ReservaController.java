package com.spacehub.controller;

import com.spacehub.service.ReservaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping("/reservas")
    public String reservas(Model model, jakarta.servlet.http.HttpSession session) {
        model.addAttribute("reservas", reservaService.listarPorUsuario((Long) session.getAttribute("usuarioId")));
        return "reservas";
    }

    @org.springframework.web.bind.annotation.InitBinder("solicitud")
    public void binder(org.springframework.web.bind.WebDataBinder binder) {
        binder.setAllowedFields("servicioId","fecha","horaInicio","horaFin");
    }

    @PostMapping("/reservas")
    public String crearReserva(@jakarta.validation.Valid @org.springframework.web.bind.annotation.ModelAttribute("solicitud")
            com.spacehub.form.SolicitudReservaForm solicitud, org.springframework.validation.BindingResult errores,
            jakarta.servlet.http.HttpSession session, RedirectAttributes flash) {
        flash.addFlashAttribute("reservaFecha",solicitud.getFecha());
        flash.addFlashAttribute("reservaInicio",solicitud.getHoraInicio());
        flash.addFlashAttribute("reservaFin",solicitud.getHoraFin());
        String regreso = solicitud.getServicioId()==null ? "redirect:/servicios" : "redirect:/servicios/"+solicitud.getServicioId();
        if (errores.hasErrors()) {
            flash.addFlashAttribute("error","Revisa la fecha y el horario: la reserva debe durar al menos una hora y terminar el mismo día.");
            return regreso;
        }
        try {
            var nueva = reservaService.crearReserva(solicitud.getServicioId(),(Long)session.getAttribute("usuarioId"),
                    solicitud.getFecha(),solicitud.getHoraInicio(),solicitud.getHoraFin());
            flash.addFlashAttribute("mensaje",String.format(Locale.US,"Solicitud #RES-%d registrada por S/ %.2f.",nueva.getId(),nueva.getTotal()));
        } catch (org.springframework.dao.ConcurrencyFailureException ex) {
            flash.addFlashAttribute("error","Otra solicitud está modificando este espacio. Intenta de nuevo."); return regreso;
        } catch (IllegalArgumentException ex) {
            flash.addFlashAttribute("error",ex.getMessage()); return regreso;
        }
        return "redirect:/reservas";
    }
}
