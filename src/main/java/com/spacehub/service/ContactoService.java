package com.spacehub.service;
import com.spacehub.model.MensajeContacto;
import com.spacehub.repository.MensajeContactoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

@Service @Transactional(readOnly = true)
public class ContactoService {
    private final MensajeContactoRepository mensajes;
    public ContactoService(MensajeContactoRepository mensajes) { this.mensajes = mensajes; }
    @Transactional public void guardar(com.spacehub.form.ContactoForm form) { mensajes.saveAndFlush(new MensajeContacto(form)); }
    public Page<MensajeContacto> listar(Boolean atendido, int pagina) {
        var pageable = PageRequest.of(Math.max(0, pagina), 20, Sort.by(Sort.Direction.DESC, "id"));
        return atendido == null ? mensajes.findAll(pageable) : mensajes.findByAtendido(atendido, pageable);
    }
    @Transactional public void estado(Long id, boolean atendido) {
        var mensaje = mensajes.findById(id).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
        mensaje.setAtendido(atendido);
    }
}
