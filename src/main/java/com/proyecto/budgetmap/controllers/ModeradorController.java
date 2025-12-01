package com.proyecto.budgetmap.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ModeradorController {

    @GetMapping("/moderador/dashboard")
    public String moderadorDashboard() {
        return "moderador/dashboard";
    }
}
