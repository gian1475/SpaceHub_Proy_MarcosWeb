package com.spacehub.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "mensajes_contacto")
public class MensajeContacto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 100) private String nombre;
    @Column(nullable = false, length = 100) private String correo;
    @Column(nullable = false, length = 20) private String motivo;
    @Column(nullable = false, length = 2000) private String mensaje;
    @Column(nullable = false) private LocalDateTime fecha;
    @Column(nullable = false) private boolean atendido;
    protected MensajeContacto() {}
    public MensajeContacto(com.spacehub.form.ContactoForm form) {
        nombre = form.getNombre(); correo = form.getCorreo(); motivo = form.getMotivo(); mensaje = form.getMensaje();
        fecha = LocalDateTime.now(java.time.ZoneId.of("America/Lima"));
    }
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getMotivo() { return motivo; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getFecha() { return fecha; }
    public boolean isAtendido() { return atendido; }
    public void setAtendido(boolean value) { atendido = value; }
}
