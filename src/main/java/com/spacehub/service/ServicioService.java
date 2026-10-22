package com.spacehub.service;

import com.spacehub.model.Categoria;
import com.spacehub.model.Servicio;
import com.spacehub.repository.CategoriaRepository;
import com.spacehub.repository.ServicioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ServicioService {
    private final ServicioRepository servicioRepository;
    private final CategoriaRepository categoriaRepository;
    private final com.spacehub.repository.ReservaRepository reservas;

    public ServicioService(ServicioRepository servicioRepository, CategoriaRepository categoriaRepository, com.spacehub.repository.ReservaRepository reservas) {
        this.servicioRepository = servicioRepository;
        this.categoriaRepository = categoriaRepository;
        this.reservas = reservas;
    }

    public List<Servicio> listarTodos() {
        return servicioRepository.findAll(Sort.by("id"));
    }

    public Servicio buscarPorId(Long id) {
        return servicioRepository.findById(id).orElse(null);
    }

    public List<Servicio> filtrarPorCategoria(String nombre) {
        if (nombre == null || nombre.isBlank() || "Todos".equalsIgnoreCase(nombre)) return listarTodos();
        return servicioRepository.findByCategoriaNombreIgnoreCaseOrderByIdAsc(nombre.trim());
    }

    public List<Servicio> obtenerDestacados() {
        return servicioRepository.findTop3ByOrderByIdAsc();
    }

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll(Sort.by("id"));
    }
    public Servicio buscarObligatorio(Long id) {
        return servicioRepository.findById(id).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "El espacio no existe."));
    }

    public com.spacehub.form.ServicioForm formulario(Long id) {
        var s = buscarObligatorio(id);
        var f = new com.spacehub.form.ServicioForm();
        f.setId(s.getId()); f.setNombre(s.getNombre()); f.setCategoriaId(s.getCategoria().getId());
        f.setDistrito(s.getDistrito()); f.setDireccion(s.getDireccion()); f.setReferencia(s.getReferencia());
        f.setPrecioPorHora(s.getPrecioPorHoraDecimal()); f.setCapacidad(s.getCapacidad()); f.setEstado(s.getEstado());
        f.setDescripcionCorta(s.getDescripcionCorta()); f.setDescripcion(s.getDescripcion());
        f.setImagenes(String.join("\n", s.getImagenes()));
        f.setComodidades(s.getServicios().stream().map(c -> (c.getIcono() == null ? "bi-check" : c.getIcono()) + "|" + c.getTexto())
                .collect(java.util.stream.Collectors.joining("\n")));
        return f;
    }

    @Transactional
    public void guardar(com.spacehub.form.ServicioForm f) {
        if (!f.isImagenesValidas() || !f.isComodidadesValidas()) throw new IllegalArgumentException("Revise las imágenes y comodidades.");
        var categoria = categoriaRepository.findById(f.getCategoriaId()).orElseThrow(() -> new IllegalArgumentException("Seleccione una categoría existente."));
        var s = f.getId() == null ? new Servicio() : buscarObligatorio(f.getId());
        s.setNombre(f.getNombre()); s.setCategoria(categoria); s.setDistrito(f.getDistrito());
        s.setDireccion(f.getDireccion()); s.setReferencia(f.getReferencia()); s.setPrecioPorHoraDecimal(f.getPrecioPorHora());
        s.setCapacidad(f.getCapacidad()); s.setEstado(f.getEstado()); s.setDescripcionCorta(f.getDescripcionCorta()); s.setDescripcion(f.getDescripcion());
        var imagenes = com.spacehub.form.ServicioForm.lineas(f.getImagenes()).stream().map(url -> {
            var imagen = new com.spacehub.model.ServicioImagen(); imagen.setUrl(url); return imagen;
        }).toList();
        s.setImagenesPersistidas(imagenes);
        s.getServicios().clear();
        for (String linea : com.spacehub.form.ServicioForm.lineas(f.getComodidades())) {
            var partes = linea.split("\\|", -1);
            var incluido = new com.spacehub.model.ServicioIncluido(partes[0].trim(), partes[1].trim());
            incluido.setServicio(s); s.getServicios().add(incluido);
        }
        servicioRepository.saveAndFlush(s);
    }

    @Transactional
    public void eliminar(Long id) {
        var s = buscarObligatorio(id);
        if (reservas.existsByServicioId(id)) throw new IllegalArgumentException("No se puede eliminar el espacio porque tiene reservas asociadas.");
        servicioRepository.delete(s);
        servicioRepository.flush();
    }
}
