package com.spacehub.service;

import com.spacehub.model.Reserva;
import com.spacehub.repository.ReservaRepository;
import com.spacehub.repository.ServicioRepository;
import com.spacehub.repository.UsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReservaService {
    private final ServicioRepository servicioRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    public ReservaService(ServicioRepository servicioRepository, ReservaRepository reservaRepository,
            UsuarioRepository usuarioRepository) {
        this.servicioRepository = servicioRepository;
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<com.spacehub.dto.ReservaListadoDTO> consultarListado(Long categoriaId, Long espacioId) {
        return reservaRepository.consultarListado(categoriaId, espacioId);
    }

    public List<Reserva> listarTodas() {
        return reservaRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<Reserva> listarPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioIdOrderByIdDesc(usuarioId);
    }

    @Transactional
    public Reserva crearReserva(Long servicioId, Long usuarioId,
            LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        var servicio = servicioRepository.bloquearPorId(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("El espacio especificado no existe."));
        validarHorario(fecha, horaInicio, horaFin, true);
        if (!"Disponible".equals(servicio.getEstado())) throw new IllegalArgumentException("El espacio no está disponible para nuevas reservas.");
        validarCruce(servicioId, fecha, horaInicio, horaFin, null);
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("La cuenta ya no existe. Inicia sesión nuevamente."));
        BigDecimal total = calcularTotal(servicio.getPrecioPorHoraDecimal(), horaInicio, horaFin);
        Reserva reserva = new Reserva(null, servicio, usuario.getNombres(), usuario.getCorreo(),
                fecha, horaInicio, horaFin, 0, "Pendiente");
        reserva.setTotalDecimal(total);
        reserva.setUsuario(usuario);
        return reservaRepository.saveAndFlush(reserva);
    }

    public Reserva buscar(Long id) {
        return reservaRepository.findById(id).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "La reserva no existe."));
    }

    public com.spacehub.form.ReservaForm formulario(Long id) {
        var r = buscar(id); var f = new com.spacehub.form.ReservaForm();
        f.setId(r.getId()); f.setFecha(r.getFecha()); f.setHoraInicio(r.getHoraInicio());
        f.setHoraFin(r.getHoraFin()); f.setEstado(r.getEstado()); return f;
    }

    private Reserva bloquearReserva(Long id) {
        var r = buscar(id);
        servicioRepository.bloquearPorId(r.getServicio().getId()).orElseThrow();
        try { entityManager.refresh(r); }
        catch (jakarta.persistence.EntityNotFoundException ex) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "La reserva ya no existe.");
        }
        return r;
    }

    private BigDecimal calcularTotal(BigDecimal precio, LocalTime inicio, LocalTime fin) {
        BigDecimal total = precio.multiply(BigDecimal.valueOf(Duration.between(inicio,fin).toSeconds()))
                .divide(BigDecimal.valueOf(3600),2,RoundingMode.HALF_UP);
        if (total.compareTo(new BigDecimal("99999999.99")) > 0)
            throw new IllegalArgumentException("El importe supera el máximo permitido. Reduce la duración de la reserva.");
        return total;
    }

    private void validarHorario(LocalDate fecha, LocalTime inicio, LocalTime fin, boolean nuevaFecha) {
        if (fecha == null || inicio == null || fin == null || !fin.isAfter(inicio))
            throw new IllegalArgumentException("Seleccione una fecha y una hora final posterior a la inicial.");
        if (Duration.between(inicio, fin).compareTo(Duration.ofHours(1)) < 0)
            throw new IllegalArgumentException("La reserva debe durar al menos una hora.");
        if (nuevaFecha && !fecha.atTime(inicio).isAfter(java.time.LocalDateTime.now(java.time.ZoneId.of("America/Lima"))))
            throw new IllegalArgumentException("La reserva debe comenzar en una fecha y hora futuras.");
    }

    private void validarCruce(Long servicioId, LocalDate fecha, LocalTime inicio, LocalTime fin, Long excluir) {
        if (!reservaRepository.conflictos(servicioId, fecha, inicio, fin, excluir).isEmpty())
            throw new IllegalArgumentException("El espacio ya tiene una reserva pendiente o aprobada en ese horario.");
    }

    // Aprobar o reactivar una solicitud exige que el espacio siga disponible.
    private void validarActivacion(Reserva reserva, String estado, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        if (!estado.equals(reserva.getEstado()) && !"Rechazada".equals(estado)) {
            validarHorario(fecha, inicio, fin, true);
            if (!"Disponible".equals(reserva.getServicio().getEstado()))
                throw new IllegalArgumentException("El espacio no está disponible para aprobar o reactivar la reserva.");
        }
    }

    @Transactional
    public void editar(com.spacehub.form.ReservaForm f) {
        var r = bloquearReserva(f.getId());
        boolean cambio = !r.getFecha().equals(f.getFecha()) || !r.getHoraInicio().equals(f.getHoraInicio()) || !r.getHoraFin().equals(f.getHoraFin());
        validarHorario(f.getFecha(), f.getHoraInicio(), f.getHoraFin(), cambio);
        if (!java.util.Set.of("Pendiente","Aprobada","Rechazada").contains(f.getEstado())) throw new IllegalArgumentException("Estado no válido.");
        validarActivacion(r, f.getEstado(), f.getFecha(), f.getHoraInicio(), f.getHoraFin());
        if (!"Rechazada".equals(f.getEstado())) {
            if (cambio && !"Disponible".equals(r.getServicio().getEstado())) throw new IllegalArgumentException("El espacio no está disponible.");
            validarCruce(r.getServicio().getId(), f.getFecha(), f.getHoraInicio(), f.getHoraFin(), r.getId());
        }
        if (cambio) r.setTotalDecimal(calcularTotal(r.getServicio().getPrecioPorHoraDecimal(), f.getHoraInicio(), f.getHoraFin()));
        r.setFecha(f.getFecha()); r.setHoraInicio(f.getHoraInicio()); r.setHoraFin(f.getHoraFin()); r.setEstado(f.getEstado());
        reservaRepository.saveAndFlush(r);
    }

    @Transactional
    public void cambiarEstado(Long id, String estado) {
        var r = bloquearReserva(id);
        if (!java.util.Set.of("Pendiente","Aprobada","Rechazada").contains(estado)) throw new IllegalArgumentException("Estado no válido.");
        validarActivacion(r, estado, r.getFecha(), r.getHoraInicio(), r.getHoraFin());
        if (!"Rechazada".equals(estado)) validarCruce(r.getServicio().getId(),r.getFecha(),r.getHoraInicio(),r.getHoraFin(),r.getId());
        r.setEstado(estado); reservaRepository.saveAndFlush(r);
    }

    @Transactional
    public void eliminar(Long id) {
        var r = bloquearReserva(id); reservaRepository.delete(r); reservaRepository.flush();
    }

    public int obtenerTotalEspacios() { return Math.toIntExact(servicioRepository.count()); }
    public int obtenerTotalReservas() { return Math.toIntExact(reservaRepository.count()); }
    public long obtenerReservasPendientesCount() { return reservaRepository.countByEstadoIgnoreCase("Pendiente"); }

    public double calcularIngresosTotales() {
        // Sumamos las solicitudes vigentes; este indicador no representa pagos recibidos.
        return reservaRepository.findByEstadoNotIgnoreCase("Rechazada").stream()
                .map(Reserva::getTotalDecimal).reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
    }
}
