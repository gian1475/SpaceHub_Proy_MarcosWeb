package com.spacehub.repository;

import com.spacehub.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "rol")
    java.util.Optional<Usuario> findOneById(Long id);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "rol")
    java.util.Optional<Usuario> findByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCase(String correo);
}
