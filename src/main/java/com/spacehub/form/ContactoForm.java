package com.spacehub.form;
import jakarta.validation.constraints.*;
public class ContactoForm {
    @NotBlank(message="El nombre es obligatorio.") @Size(min=2,max=100)
    private String nombre;
    public String getNombre() { return nombre; }
    public void setNombre(String valor) { this.nombre=valor; }
    @NotBlank @Email(message="Ingrese un correo v\u00e1lido.") @Size(max=100)
    private String correo;
    public String getCorreo() { return correo; }
    public void setCorreo(String valor) { this.correo=valor; }
    @NotBlank @Pattern(regexp="reserva|publicar|soporte",message="Seleccione un motivo v\u00e1lido.")
    private String motivo;
    public String getMotivo() { return motivo; }
    public void setMotivo(String valor) { this.motivo=valor; }
    @NotBlank(message="El mensaje es obligatorio.") @Size(min=10,max=2000,message="El mensaje debe tener entre 10 y 2000 caracteres.")
    private String mensaje;
    public String getMensaje() { return mensaje; }
    public void setMensaje(String valor) { this.mensaje=valor; }
}
