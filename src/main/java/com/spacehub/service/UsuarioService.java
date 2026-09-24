package com.spacehub.service;

import com.spacehub.model.Rol;
import com.spacehub.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UsuarioService {

    private final List<Usuario> usuarios = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(1);

    public UsuarioService() {
        // Usuario Admin inicial de prueba
        // NOTA: La contraseña va en texto plano solo hasta la Entrega 4 (cuando se implemente Spring Security con BCrypt)
        usuarios.add(new Usuario(
                contadorId.getAndIncrement(),
                "Admin",
                "SpaceHub",
                "admin@spacehub.pe",
                "999888777",
                "admin123",
                Rol.ADMIN
        ));
    }

    public boolean registrarUsuario(String nombres, String apellidos, String correo, String telefono, String tipo, String password) {
        if (correoExiste(correo)) {
            return false;
        }

        Rol rol = "propietario".equalsIgnoreCase(tipo) ? Rol.PROPIETARIO : Rol.CLIENTE;

        Usuario nuevo = new Usuario(
                contadorId.getAndIncrement(),
                nombres,
                apellidos,
                correo,
                telefono,
                password,
                rol
        );

        usuarios.add(nuevo);
        return true;
    }

    public boolean correoExiste(String correo) {
        if (correo == null) return false;
        return usuarios.stream().anyMatch(u -> u.getCorreo().equalsIgnoreCase(correo.trim()));
    }

    public Optional<Usuario> validarLogin(String correo, String password) {
        if (correo == null || password == null) return Optional.empty();
        return usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo.trim()) && u.getPassword().equals(password))
                .findFirst();
    }
}
