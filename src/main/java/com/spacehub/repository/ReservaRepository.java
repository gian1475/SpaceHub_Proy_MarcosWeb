package com.spacehub.repository;

import com.spacehub.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select r from Reserva r where r.servicio.id = :servicioId and r.fecha = :fecha " +
            "and r.estado in ('Pendiente','Aprobada') and (:excluir is null or r.id <> :excluir) " +
            "and r.horaInicio < :fin and r.horaFin > :inicio")
    java.util.List<Reserva> conflictos(@org.springframework.data.repository.query.Param("servicioId") Long servicioId,
            @org.springframework.data.repository.query.Param("fecha") java.time.LocalDate fecha,
            @org.springframework.data.repository.query.Param("inicio") java.time.LocalTime inicio,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalTime fin,
            @org.springframework.data.repository.query.Param("excluir") Long excluir);
    java.util.List<Reserva> findByUsuarioIdOrderByIdDesc(Long usuarioId);
    long countByEstadoIgnoreCase(String estado);
    java.util.List<Reserva> findByEstadoNotIgnoreCase(String estado);
    boolean existsByServicioId(Long servicioId);
}
