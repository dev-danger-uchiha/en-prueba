package com.proyecto.budgetmap.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClienteController {

    @GetMapping("/cliente/dashboard")
    public String clienteDashboard() {
        return "cliente/dashboard";
    }
}