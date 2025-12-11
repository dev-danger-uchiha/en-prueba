package com.proyecto.budgetmap.controllers;

import com.proyecto.budgetmap.models.*;
import com.proyecto.budgetmap.models.enums.EstadoEstablecimiento;
import com.proyecto.budgetmap.models.enums.EstadoLugar;
import com.proyecto.budgetmap.repositories.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    private final LugarRepository lugarRepository;
    private final EventoRepository eventoRepository;
    private final EstablecimientoRepository establecimientoRepository;
    private final PqrsRepository pqrsRepository;

    public ClienteController(
            LugarRepository lugarRepository,
            EventoRepository eventoRepository,
            EstablecimientoRepository establecimientoRepository,
            PqrsRepository pqrsRepository) {

        this.lugarRepository = lugarRepository;
        this.eventoRepository = eventoRepository;
        this.establecimientoRepository = establecimientoRepository;
        this.pqrsRepository = pqrsRepository;
    }

    // DASHBOARD CLIENTE
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("lugares",
                lugarRepository.findByEstado(EstadoLugar.PUBLICADO));

        model.addAttribute("eventos",
                eventoRepository.findAll());

        model.addAttribute("establecimientos",
                establecimientoRepository.findByEstado(EstadoEstablecimiento.APROBADO));

        return "cliente/dashboard";
    }

    // -----------------------------------------
    // LISTA DE LUGARES
    // -----------------------------------------
    @GetMapping("/lugares")
    public String lugares(Model model) {
        model.addAttribute("lugares", lugarRepository.findByEstado(EstadoLugar.PUBLICADO));
        return "cliente/l_gestion/listar";
    }

    @GetMapping("/lugares/{id}")
    public String verLugar(@PathVariable Long id, Model model) {
        Lugar lugar = lugarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lugar no encontrado"));

        model.addAttribute("lugar", lugar);
        return "cliente/l_gestion/ver";
    }

    // -----------------------------------------
    // ESTABLECIMIENTOS
    // -----------------------------------------
    @GetMapping("/establecimientos")
    public String establecimientos(Model model) {
        model.addAttribute("establecimientos",
                establecimientoRepository.findByEstado(EstadoEstablecimiento.APROBADO));

        return "cliente/e_gestion/listar";
    }

    @GetMapping("/establecimientos/{id}")
    public String verEstablecimiento(@PathVariable Long id, Model model) {
        model.addAttribute("establecimiento",
                establecimientoRepository.findById(id).orElseThrow());

        return "cliente/e_gestion/ver";
    }

    // -----------------------------------------
    // EVENTOS
    // -----------------------------------------
    @GetMapping("/eventos")
    public String eventos(Model model) {
        model.addAttribute("eventos", eventoRepository.findAll());
        return "cliente/ev_gestion/listar";
    }

    @GetMapping("/eventos/{id}")
    public String verEvento(@PathVariable Long id, Model model) {
        model.addAttribute("evento", eventoRepository.findById(id).orElseThrow());
        return "cliente/ev_gestion/ver";
    }

    // -----------------------------------------
    // PQRS
    // -----------------------------------------
    @GetMapping("/pqrs")
    public String pqrs(Model model) {
        model.addAttribute("pqrsList", pqrsRepository.findAll());
        return "cliente/pqrs/listar";
    }

    @GetMapping("/pqrs/crear")
    public String crearPqrs(Model model) {
        model.addAttribute("pqrs", new PQRS());
        return "cliente/pqrs/crear";
    }

    @PostMapping("/pqrs/crear")
    public String guardarPqrs(@ModelAttribute PQRS pqrs) {
        pqrsRepository.save(pqrs);
        return "redirect:/cliente/pqrs";
    }

    @GetMapping("/pqrs/{id}")
    public String verPqrs(@PathVariable Long id, Model model) {
        model.addAttribute("pqrs", pqrsRepository.findById(id).orElseThrow());
        return "cliente/pqrs/ver";
    }
}
