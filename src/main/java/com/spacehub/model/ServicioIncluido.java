package com.spacehub.model;

public class ServicioIncluido {
    private String icono;
    private String texto;

    public ServicioIncluido() {
    }

    public ServicioIncluido(String icono, String texto) {
        this.icono = icono;
        this.texto = texto;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
