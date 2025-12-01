package com.proyecto.budgetmap.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EstablecimientoController {

    @GetMapping("/establecimiento/dashboard")
    public String establecimientoDashboard() {
        return "establecimiento/dashboard";
    }
}
