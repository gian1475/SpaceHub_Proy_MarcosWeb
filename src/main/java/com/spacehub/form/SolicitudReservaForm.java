package com.spacehub.form;
public class SolicitudReservaForm extends ReservaForm {
    @jakarta.validation.constraints.NotNull(message="Seleccione un espacio.")
    private Long servicioId;
    public Long getServicioId() { return servicioId; }
    public void setServicioId(Long servicioId) { this.servicioId=servicioId; }
}
