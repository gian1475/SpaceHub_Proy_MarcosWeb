package com.spacehub.repository;
import com.spacehub.model.MensajeContacto;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MensajeContactoRepository extends JpaRepository<MensajeContacto, Long> {
    org.springframework.data.domain.Page<MensajeContacto> findByAtendido(boolean atendido, org.springframework.data.domain.Pageable pagina);
}
