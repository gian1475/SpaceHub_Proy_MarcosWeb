package com.spacehub.service;

import com.spacehub.model.Usuario;
import com.spacehub.repository.RolRepository;
import com.spacehub.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository, org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.encoder = encoder;
    }

    @Transactional
    public boolean registrarUsuario(String nombres, String apellidos, String correo,
            String telefono, String tipo, String password) {
        if (correoExiste(correo)) return false;
        String nombreRol = "propietario".equalsIgnoreCase(tipo) ? "PROPIETARIO" : "CLIENTE";
        var rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new IllegalStateException("Faltan los roles iniciales. Ejecute el script de datos."));
        // Las cuentas nuevas siempre usan hash.
        usuarioRepository.save(new Usuario(null, nombres, apellidos,
                correo.trim().toLowerCase(Locale.ROOT), telefono, encoder.encode(password), rol));
        return true;
    }

    public boolean correoExiste(String correo) {
        return correo != null && usuarioRepository.existsByCorreoIgnoreCase(correo.trim());
    }

    @Transactional
    public Optional<Usuario> validarLogin(String correo, String password) {
        if (correo == null || password == null) return Optional.empty();
        var encontrado = usuarioRepository.findByCorreoIgnoreCase(correo.trim());
        if (encontrado.isEmpty()) return Optional.empty();
        var usuario = encontrado.get();
        String guardada = usuario.getPassword();
        if (guardada.startsWith("$2")) {
            return encoder.matches(password, guardada) ? encontrado : Optional.empty();
        }
        // Migracion progresiva de cuentas existentes, solo tras verificar la clave anterior.
        if (!java.security.MessageDigest.isEqual(password.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                guardada.getBytes(java.nio.charset.StandardCharsets.UTF_8))) return Optional.empty();
        usuario.setPassword(encoder.encode(password));
        usuarioRepository.save(usuario);
        return encontrado;
    }
}
