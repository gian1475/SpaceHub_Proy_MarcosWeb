package com.spacehub.repository;

import com.spacehub.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
    boolean existsByNombreIgnoreCase(String nombre);
}
