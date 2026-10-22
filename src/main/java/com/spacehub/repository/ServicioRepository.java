package com.spacehub.repository;

import com.spacehub.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    java.util.List<Servicio> findByCategoriaIdOrderByNombreAsc(Long categoriaId);
    @org.springframework.data.jpa.repository.Query("""
        select s from Servicio s join fetch s.categoria c
        where (:categoriaId is null or c.id = :categoriaId)
          and (:espacioId is null or s.id = :espacioId)
        order by s.nombre
        """)
    java.util.List<Servicio> consultarCatalogo(
        @org.springframework.data.repository.query.Param("categoriaId") Long categoriaId,
        @org.springframework.data.repository.query.Param("espacioId") Long espacioId);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select s from Servicio s where s.id = :id")
    java.util.Optional<Servicio> bloquearPorId(@org.springframework.data.repository.query.Param("id") Long id);
    java.util.List<Servicio> findByCategoriaId(Long categoriaId);
    java.util.List<Servicio> findByCategoriaNombreIgnoreCaseOrderByIdAsc(String nombre);
    java.util.List<Servicio> findTop3ByOrderByIdAsc();
    boolean existsByCategoriaId(Long categoriaId);
}
