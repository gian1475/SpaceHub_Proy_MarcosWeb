package com.spacehub.form;
import jakarta.validation.constraints.*;
public class RegistroForm {
    @NotBlank @Size(min=2,max=80)
    private String nombres;
    public String getNombres() { return nombres; }
    public void setNombres(String valor) { this.nombres = valor; }
    @NotBlank @Size(min=2,max=80)
    private String apellidos;
    public String getApellidos() { return apellidos; }
    public void setApellidos(String valor) { this.apellidos = valor; }
    @NotBlank @Email @Size(max=100)
    private String correo;
    public String getCorreo() { return correo; }
    public void setCorreo(String valor) { this.correo = valor; }
    @NotBlank @Pattern(regexp="[0-9]{9}",message="El teléfono debe tener 9 dígitos.")
    private String telefono;
    public String getTelefono() { return telefono; }
    public void setTelefono(String valor) { this.telefono = valor; }
    @NotBlank @Pattern(regexp="cliente|propietario")
    private String tipo = "cliente";
    public String getTipo() { return tipo; }
    public void setTipo(String valor) { this.tipo = valor; }
    @NotBlank @Size(min=8,max=72,message="La contraseña debe tener entre 8 y 72 caracteres.")
    private String password;
    public String getPassword() { return password; }
    public void setPassword(String valor) { this.password = valor; }
    @AssertTrue(message="La contrase\u00f1a supera los 72 bytes UTF-8 admitidos.")
    public boolean isPasswordLongitudValida() { return password == null || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 72; }
    @AssertTrue(message="Acepta los términos y condiciones.")
    private boolean terminos;
    public boolean isTerminos() { return terminos; }
    public void setTerminos(boolean terminos) { this.terminos = terminos; }
}
