package com.spacehub.config;

import com.spacehub.repository.UsuarioRepository;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import java.util.*;

@Configuration
public class SeguridadApi {
    @Bean SecretKey claveJwt(@Value("${SPACEHUB_JWT_SECRET:}") String configurada) {
        byte[] bytes;
        if (configurada.isBlank()) {
            bytes = new byte[32]; new java.security.SecureRandom().nextBytes(bytes);
        } else {
            bytes = Base64.getDecoder().decode(configurada);
            if (bytes.length < 32) throw new IllegalArgumentException("SPACEHUB_JWT_SECRET requiere al menos 32 bytes en Base64.");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }
    @Bean JwtEncoder jwtEncoder(SecretKey claveJwt) {
        return new NimbusJwtEncoder(new com.nimbusds.jose.jwk.source.ImmutableSecret<>(claveJwt));
    }
    @Bean JwtDecoder jwtDecoder(SecretKey claveJwt) {
        var decoder = NimbusJwtDecoder.withSecretKey(claveJwt).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("spacehub"));
        return decoder;
    }
    @Bean @Order(1)
    SecurityFilterChain api(HttpSecurity http, UsuarioRepository usuarios) throws Exception {
        http.securityMatcher("/api/**")
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(c -> c.disable()).requestCache(c -> c.disable())
            .authorizeHttpRequests(a -> a
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(o -> o
                .authenticationEntryPoint((request,response,ex) -> error(response,401,"Se requiere un token válido."))
                .accessDeniedHandler((request,response,ex) -> error(response,403,"No tienes permiso para esta operación."))
                .jwt(j -> j.jwtAuthenticationConverter(jwt -> {
                try {
                    var usuario = usuarios.findOneById(Long.valueOf(jwt.getSubject())).orElseThrow();
                    Object version = jwt.getClaims().get("version");
                    if (!usuario.isActivo() || jwt.getExpiresAt() == null || !(version instanceof Number numero)
                            || new java.math.BigDecimal(numero.toString()).compareTo(java.math.BigDecimal.valueOf(usuario.getVersionToken())) != 0)
                        throw new IllegalArgumentException();
                    return new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre())));
                } catch (IllegalArgumentException | NoSuchElementException ex) {
                    throw new InvalidBearerTokenException("La cuenta o el token ya no están habilitados.");
                }
            })))
            .exceptionHandling(e -> e
                .authenticationEntryPoint((request,response,ex) -> error(response,401,"Se requiere un token válido."))
                .accessDeniedHandler((request,response,ex) -> error(response,403,"No tienes permiso para esta operación.")));
        return http.build();
    }
    private static void error(jakarta.servlet.http.HttpServletResponse response, int status, String mensaje) throws java.io.IOException {
        response.setStatus(status); response.setContentType("application/json;charset=UTF-8");
        if (status == 401) response.setHeader("WWW-Authenticate", "Bearer");
        response.getWriter().write("{\"mensaje\":\"" + mensaje + "\"}");
    }
}
