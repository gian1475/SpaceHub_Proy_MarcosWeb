package com.spacehub.config;

import com.spacehub.repository.UsuarioRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.*;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.*;

@Configuration
public class SeguridadWeb {
    @Bean public BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }
    @Bean
    org.springframework.security.authentication.AuthenticationManager autenticacion(com.spacehub.service.UsuarioService usuarios) {
        var provider = new org.springframework.security.authentication.AuthenticationProvider() {
            public org.springframework.security.core.Authentication authenticate(org.springframework.security.core.Authentication entrada) {
                String correo = entrada.getName();
                String password = entrada.getCredentials() instanceof String clave ? clave : "";
                if (correo.length() > 100 || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72 || correo.isBlank() || password.isEmpty())
                    throw new org.springframework.security.authentication.BadCredentialsException("Credenciales incorrectas");
                var usuario = usuarios.validarLogin(correo, password).orElseThrow(() ->
                        new org.springframework.security.authentication.BadCredentialsException("Credenciales incorrectas"));
                return new UsernamePasswordAuthenticationToken(usuario, null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre())));
            }
            public boolean supports(Class<?> tipo) { return UsernamePasswordAuthenticationToken.class.isAssignableFrom(tipo); }
        };
        return new org.springframework.security.authentication.ProviderManager(provider);
    }
    public static String token(HttpSession session) {
        if (session.getAttribute("csrf") == null) session.setAttribute("csrf", UUID.randomUUID().toString());
        return (String) session.getAttribute("csrf");
    }
    @Bean
    SecurityFilterChain seguridad(HttpSecurity http, UsuarioRepository usuarios) throws Exception {
        // Conserva los formularios existentes; CsrfFilter valida el token de sesion.
        var csrf = new CsrfTokenRepository() {
            public CsrfToken generateToken(HttpServletRequest request) {
                return new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", UUID.randomUUID().toString());
            }
            public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
                if (token != null) request.getSession().setAttribute("csrf", token.getToken());
                else if (request.getSession(false) != null) request.getSession(false).removeAttribute("csrf");
            }
            public CsrfToken loadToken(HttpServletRequest request) {
                var session = request.getSession(false);
                return session != null && session.getAttribute("csrf") instanceof String value
                        ? new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", value) : null;
            }
        };
        var sesion = new OncePerRequestFilter() {
            @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                    FilterChain chain) throws java.io.IOException, ServletException {
                var csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                if (csrfToken != null) csrfToken.getToken();
                var session = request.getSession(false);
                // El login valida credenciales y renueva la sesion. Releer el rol permite revocarlo inmediatamente.
                if (session != null && session.getAttribute("usuarioId") instanceof Long id) {
                    var usuario = usuarios.findOneById(id).orElse(null);
                    if (usuario == null) {
                        session.removeAttribute("usuarioId"); session.removeAttribute("usuarioNombre"); session.removeAttribute("usuarioRol");
                        SecurityContextHolder.clearContext();
                    } else {
                        session.setAttribute("usuarioNombre", usuario.getNombres());
                        session.setAttribute("usuarioRol", usuario.getRol().getNombre());
                        var context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(new UsernamePasswordAuthenticationToken(id, null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre()))));
                        SecurityContextHolder.setContext(context);
                    }
                }
                chain.doFilter(request, response);
            }
        };
        http.csrf(c -> c.csrfTokenRepository(csrf).csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
            .addFilterBefore(sesion, AnonymousAuthenticationFilter.class)
            .authorizeHttpRequests(a -> a
                .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()
                .requestMatchers("/administracion", "/administracion/**", "/admin", "/admin/**").hasRole("ADMIN")
                .requestMatchers("/propietario", "/propietario/**").hasRole("PROPIETARIO")
                .requestMatchers("/reservas", "/reservas/**").authenticated()
                .anyRequest().permitAll())
            .exceptionHandling(e -> e.authenticationEntryPoint((request, response, ex) ->
                    response.sendRedirect(request.getContextPath() + "/login")))
            .requestCache(c -> c.disable())
            .formLogin(c -> c.disable()).httpBasic(c -> c.disable()).logout(c -> c.disable())
            .headers(h -> h.frameOptions(f -> f.sameOrigin()));
        return http.build();
    }
}
