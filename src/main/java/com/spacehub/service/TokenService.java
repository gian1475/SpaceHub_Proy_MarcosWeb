package com.spacehub.service;

import com.spacehub.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import java.time.Instant;

@Service
public class TokenService {
    private final JwtEncoder encoder;
    public TokenService(JwtEncoder encoder) { this.encoder = encoder; }
    public record Token(String access_token, String token_type, long expires_in) {}
    public Token emitir(Usuario usuario) {
        var ahora = Instant.now();
        var claims = JwtClaimsSet.builder().issuer("spacehub").subject(usuario.getId().toString())
                .issuedAt(ahora).expiresAt(ahora.plusSeconds(3600)).id(java.util.UUID.randomUUID().toString())
                .claim("rol", usuario.getRol().getNombre()).claim("version", usuario.getVersionToken()).build();
        var jwt = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims));
        return new Token(jwt.getTokenValue(), "Bearer", 3600);
    }
}
