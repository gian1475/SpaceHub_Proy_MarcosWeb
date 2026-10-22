package com.spacehub.repository;

import com.spacehub.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @org.springframework.data.jpa.repository.Query("""
        select new com.spacehub.dto.ReservaListadoDTO(
            r.id, concat(concat(u.nombres, ' '), u.apellidos), u.correo,
            s.nombre, c.nombre, r.fecha, r.horaInicio, r.horaFin, r.total, r.estado)
        from Reserva r join r.usuario u join r.servicio s join s.categoria c
        where (:categoriaId is null or c.id = :categoriaId)
          and (:espacioId is null or s.id = :espacioId)
        order by r.fecha desc, r.horaInicio desc, r.id desc
        """)
    java.util.List<com.spacehub.dto.ReservaListadoDTO> consultarListado(
        @org.springframework.data.repository.query.Param("categoriaId") Long categoriaId,
        @org.springframework.data.repository.query.Param("espacioId") Long espacioId);

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
