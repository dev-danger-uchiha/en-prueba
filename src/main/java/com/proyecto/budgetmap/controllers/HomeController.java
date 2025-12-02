package com.proyecto.budgetmap.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({ "/" })
    public String showHomePage() {
        return "public/home";
    }
}