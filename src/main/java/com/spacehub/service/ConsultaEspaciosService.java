package com.spacehub.service;

import com.spacehub.model.Categoria;
import com.spacehub.model.Servicio;
import com.spacehub.repository.CategoriaRepository;
import com.spacehub.repository.ServicioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConsultaEspaciosService {
    private final CategoriaRepository categorias;
    private final ServicioRepository espacios;
    public ConsultaEspaciosService(CategoriaRepository categorias, ServicioRepository espacios) {
        this.categorias = categorias;
        this.espacios = espacios;
    }
    public Long categoriaPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank() || "Todos".equalsIgnoreCase(nombre)) return null;
        return categorias.findByNombreIgnoreCase(nombre.trim()).map(Categoria::getId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría no existe."));
    }
    public void preparar(Long categoriaId, Long espacioId, Model model) {
        if (categoriaId != null && !categorias.existsById(categoriaId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría no existe.");
        List<Servicio> opciones = categoriaId == null ? List.of() : espacios.findByCategoriaIdOrderByNombreAsc(categoriaId);
        if (espacioId != null && opciones.stream().noneMatch(s -> s.getId().equals(espacioId)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El espacio no pertenece a la categoría seleccionada.");
        model.addAttribute("categorias", categorias.findAll(org.springframework.data.domain.Sort.by("nombre")));
        model.addAttribute("espaciosFiltro", opciones);
        model.addAttribute("categoriaId", categoriaId);
        model.addAttribute("espacioId", espacioId);
    }
    public List<Servicio> consultar(Long categoriaId, Long espacioId) {
        return espacios.consultarCatalogo(categoriaId, espacioId);
    }
}
