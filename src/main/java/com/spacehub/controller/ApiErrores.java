package com.spacehub.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

@RestControllerAdvice(assignableTypes = {ReservaApiController.class, AuthApiController.class, CatalogoApiController.class})
public class ApiErrores {
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    ResponseEntity<?> jsonInvalido() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(java.util.Map.of("mensaje","El JSON o el formato de fecha y hora no es válido.")); }
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    ResponseEntity<?> estado(org.springframework.web.server.ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(java.util.Map.of("mensaje","El recurso o los parámetros solicitados no son válidos."));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> datos(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(java.util.Map.of("mensaje", "Datos inválidos. Revisa el espacio, fecha, horario y disponibilidad."));
    }
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    ResponseEntity<?> validacion() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(java.util.Map.of("mensaje","Revisa los campos enviados.")); }
    @ExceptionHandler(org.springframework.dao.ConcurrencyFailureException.class)
    ResponseEntity<?> conflicto() { return ResponseEntity.status(409).contentType(MediaType.APPLICATION_JSON).body(java.util.Map.of("mensaje","El espacio está siendo modificado. Intenta de nuevo.")); }
}
