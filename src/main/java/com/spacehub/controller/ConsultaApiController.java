package com.spacehub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConsultaApiController {
    @GetMapping("/consulta-api")
    public String consulta() { return "consulta-api"; }
}
