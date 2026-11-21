package com.spacehub.service;

import com.spacehub.model.Usuario;
import com.spacehub.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service @Transactional(readOnly = true)
public class AdminUsuarioService {
    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    public AdminUsuarioService(UsuarioRepository usuarios, RolRepository roles) { this.usuarios = usuarios; this.roles = roles; }
    public Page<Usuario> listar(int pagina) { return usuarios.findAll(PageRequest.of(Math.max(0,pagina),20,Sort.by("id"))); }
    @Transactional
    public void actualizar(Long id, String rol, boolean activo, Long administrador) {
        if (!java.util.Set.of("ADMIN","CLIENTE","PROPIETARIO").contains(rol)) throw new IllegalArgumentException("Seleccione un rol válido.");
        // Serializa los cambios administrativos para no dejar el sistema sin administradores por cambios simultáneos.
        roles.bloquearPorNombre("ADMIN").orElseThrow();
        var actor = usuarios.findOneById(administrador).orElseThrow();
        if (!actor.isActivo() || !"ADMIN".equals(actor.getRol().getNombre())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        var usuario = usuarios.findOneById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (id.equals(administrador) && (!activo || !"ADMIN".equals(rol)))
            throw new IllegalArgumentException("No puedes desactivar tu propia cuenta ni quitarte el rol administrador.");
        if (usuario.isActivo() != activo || !usuario.getRol().getNombre().equals(rol)) {
            usuario.setRol(roles.findByNombre(rol).orElseThrow(() -> new IllegalArgumentException("El rol no está registrado.")));
            usuario.setActivo(activo); usuario.revocarTokens(); usuarios.saveAndFlush(usuario);
        }
    }
}
