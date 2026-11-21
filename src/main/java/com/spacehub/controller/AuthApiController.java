package com.spacehub.controller;

import com.spacehub.service.TokenService;
import com.spacehub.repository.UsuarioRepository;
import com.spacehub.model.Usuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.http.*;

@RestController
public class AuthApiController {
    private final AuthenticationManager autenticacion;
    private final TokenService tokens;
    private final UsuarioRepository usuarios;
    public AuthApiController(AuthenticationManager autenticacion, TokenService tokens, UsuarioRepository usuarios) {
        this.autenticacion = autenticacion; this.tokens = tokens; this.usuarios = usuarios;
    }
    public record Login(@NotBlank @Email @Size(max=100) String correo, @NotBlank @Size(max=72) String password) {}
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody Login entrada) {
        try {
            var acceso = autenticacion.authenticate(new UsernamePasswordAuthenticationToken(entrada.correo(),entrada.password()));
            return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(tokens.emitir((Usuario) acceso.getPrincipal()));
        } catch (org.springframework.security.core.AuthenticationException ex) {
            return ResponseEntity.status(401).body(java.util.Map.of("mensaje","Correo o contraseña incorrectos."));
        }
    }
    @PostMapping("/sesion/token-api")
    public ResponseEntity<TokenService.Token> tokenDeSesion(jakarta.servlet.http.HttpSession session) {
        var usuario = usuarios.findOneById((Long) session.getAttribute("usuarioId")).orElseThrow();
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(tokens.emitir(usuario));
    }
}
