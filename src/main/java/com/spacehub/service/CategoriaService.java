package com.spacehub.service;

import com.spacehub.model.Categoria;
import com.spacehub.repository.CategoriaRepository;
import com.spacehub.repository.ServicioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoriaService {
    private final CategoriaRepository categorias;
    private final ServicioRepository servicios;
    public CategoriaService(CategoriaRepository categorias, ServicioRepository servicios) {
        this.categorias = categorias;
        this.servicios = servicios;
    }
    public List<Categoria> listar() { return categorias.findAll(Sort.by("id")); }
    public Categoria buscar(Long id) {
        return categorias.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "La categoría no existe."));
    }
    public boolean nombreExiste(String nombre, Long id) {
        return id == null ? categorias.existsByNombreIgnoreCase(nombre)
                : categorias.existsByNombreIgnoreCaseAndIdNot(nombre, id);
    }
    @Transactional
    public void guardar(Categoria datos) {
        if (nombreExiste(datos.getNombre(), datos.getId())) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre.");
        }
        Categoria destino = datos.getId() == null ? new Categoria() : buscar(datos.getId());
        destino.setNombre(datos.getNombre());
        destino.setIcono(datos.getIcono());
        destino.setDescripcion(datos.getDescripcion());
        categorias.saveAndFlush(destino);
    }
    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscar(id);
        if (servicios.existsByCategoriaId(id)) {
            throw new IllegalArgumentException("No se puede eliminar la categoría porque tiene espacios asociados.");
        }
        categorias.delete(categoria);
        categorias.flush();
    }
}
