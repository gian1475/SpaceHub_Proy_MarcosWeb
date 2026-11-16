package com.spacehub.controller;

import com.spacehub.service.ServicioService;
import com.spacehub.model.Servicio;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;

/** API pública de consulta: nunca serializa entidades ni datos de usuarios. */
@RestController
@RequestMapping("/api/v1")
@Transactional(readOnly = true)
public class CatalogoApiController {
    private final ServicioService servicios;
    public CatalogoApiController(ServicioService servicios) { this.servicios = servicios; }

    public record CategoriaDTO(Long id, String nombre, String descripcion) {}
    public record EspacioDTO(Long id, String nombre, Long categoriaId, String categoria,
            String distrito, BigDecimal precioPorHora, String moneda, int capacidad,
            String estado, String descripcion) {
        static EspacioDTO desde(Servicio s) {
            return new EspacioDTO(s.getId(), s.getNombre(), s.getCategoria().getId(),
                    s.getCategoria().getNombre(), s.getDistrito(), s.getPrecioPorHoraDecimal(),
                    "PEN", s.getCapacidad(), s.getEstado(), s.getDescripcionCorta());
        }
    }

    @GetMapping("/categorias")
    public List<CategoriaDTO> categorias() {
        return servicios.listarCategorias().stream()
                .map(c -> new CategoriaDTO(c.getId(), c.getNombre(), c.getDescripcion())).toList();
    }

    @GetMapping("/espacios")
    public List<EspacioDTO> espacios(@RequestParam(required = false) Long categoriaId) {
        if (categoriaId != null && servicios.listarCategorias().stream().noneMatch(c -> c.getId().equals(categoriaId)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría no existe.");
        return servicios.listarTodos().stream()
                .filter(s -> categoriaId == null || s.getCategoria().getId().equals(categoriaId))
                .map(EspacioDTO::desde).toList();
    }

    @GetMapping("/espacios/{id}")
    public EspacioDTO espacio(@PathVariable Long id) { return EspacioDTO.desde(servicios.buscarObligatorio(id)); }
}
