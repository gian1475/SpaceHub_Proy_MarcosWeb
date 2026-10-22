package com.spacehub.config;

import com.spacehub.repository.UsuarioRepository;
import jakarta.servlet.http.*;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.UUID;

@Configuration
public class SeguridadWeb implements WebMvcConfigurer {
    private final UsuarioRepository usuarios;
    public SeguridadWeb(UsuarioRepository usuarios) { this.usuarios = usuarios; }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }
    public static String token(HttpSession session) {
        if (session.getAttribute("csrf") == null) session.setAttribute("csrf", UUID.randomUUID().toString());
        return (String) session.getAttribute("csrf");
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                if (!(handler instanceof org.springframework.web.method.HandlerMethod)) return true;
                var session = request.getSession();
                String token = token(session);
                if (!java.util.Set.of("GET", "HEAD", "OPTIONS").contains(request.getMethod())
                        && !token.equals(request.getParameter("_csrf"))) {
                    response.sendError(403, "Formulario vencido. Recargue la pagina."); return false;
                }
                String ruta = (String) request.getAttribute(org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
                if (ruta == null) ruta = request.getServletPath();
                Object id = session.getAttribute("usuarioId");
                if (id instanceof Long usuarioId) {
                    var usuario = usuarios.findOneById(usuarioId).orElse(null);
                    if (usuario == null) {
                        session.removeAttribute("usuarioId"); session.removeAttribute("usuarioNombre"); session.removeAttribute("usuarioRol");
                        id = null;
                    } else {
                        session.setAttribute("usuarioNombre", usuario.getNombres());
                        session.setAttribute("usuarioRol", usuario.getRol().getNombre());
                    }
                } else id = null;
                boolean admin = ruta.equals("/administracion") || ruta.startsWith("/administracion/")
                        || ruta.equals("/admin") || ruta.startsWith("/admin/");
                boolean privada = admin || ruta.equals("/reservas") || ruta.startsWith("/reservas/");
                if (privada && id == null) { response.sendRedirect(request.getContextPath() + "/login"); return false; }
                if (admin && !"ADMIN".equals(session.getAttribute("usuarioRol"))) {
                    response.sendError(403, "Esta pagina es solo para administradores."); return false;
                }
                if (privada) response.setHeader("Cache-Control", "no-store");
                return true;
            }
        });
    }
}
