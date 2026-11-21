package com.spacehub.repository;

import com.spacehub.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select r from Rol r where r.nombre = :nombre")
    java.util.Optional<Rol> bloquearPorNombre(@org.springframework.data.repository.query.Param("nombre") String nombre);
    java.util.Optional<Rol> findByNombre(String nombre);
}
