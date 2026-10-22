package com.spacehub.repository;

import com.spacehub.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select s from Servicio s where s.id = :id")
    java.util.Optional<Servicio> bloquearPorId(@org.springframework.data.repository.query.Param("id") Long id);
    java.util.List<Servicio> findByCategoriaId(Long categoriaId);
    java.util.List<Servicio> findByCategoriaNombreIgnoreCaseOrderByIdAsc(String nombre);
    java.util.List<Servicio> findTop3ByOrderByIdAsc();
    boolean existsByCategoriaId(Long categoriaId);
}
