package com.proyecto.budgetmap.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.proyecto.budgetmap.models.*;
import com.proyecto.budgetmap.models.enums.*;
import com.proyecto.budgetmap.repositories.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/moderador")
public class ModeradorController {

    private final LugarRepository lugarRepository;
    private final EventoRepository eventoRepository;
    private final EstablecimientoRepository establecimientoRepository;

    public ModeradorController(
            LugarRepository lugarRepository,
            EventoRepository eventoRepository,
            EstablecimientoRepository establecimientoRepository) {

        this.lugarRepository = lugarRepository;
        this.eventoRepository = eventoRepository;
        this.establecimientoRepository = establecimientoRepository;
    }

    // ============================================================
    // DASHBOARD PRINCIPAL
    // ============================================================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("lugaresCreados",
                lugarRepository.count());

        model.addAttribute("eventosActivos",
                eventoRepository.count());

        model.addAttribute("establecimientosPendientes",
                establecimientoRepository.countByEstado(EstadoEstablecimiento.PENDIENTE));

        return "moderador/dashboard";
    }

    // ============================================================
    // GESTIÓN DE LUGARES (CRUD COMPLETO)
    // ============================================================
    @GetMapping("/lugares")
    public String lugares(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            Model model) {

        List<Lugar> lugares = lugarRepository.findAll();

        lugares = filtrarLugares(lugares, search, tipo, estado);

        model.addAttribute("listaLugares", lugares);
        model.addAttribute("search", search);
        model.addAttribute("tipo", tipo);
        model.addAttribute("estado", estado);

        model.addAttribute("tipos", TipoLugar.values());
        model.addAttribute("estados", EstadoLugar.values());

        return "moderador/l_gestion/listar";
    }

    @GetMapping("/lugares/crear")
    public String crearLugar(Model model) {
        model.addAttribute("lugar", new Lugar());
        model.addAttribute("tipos", TipoLugar.values());
        return "moderador/l_gestion/crear";
    }

    @PostMapping("/lugares/crear")
    public String guardarLugar(@ModelAttribute Lugar lugar) {
        lugar.setEstado(EstadoLugar.BORRADOR);
        lugarRepository.save(lugar);
        return "redirect:/moderador/lugares";
    }

    @GetMapping("/lugares/{id}")
    public String verLugar(@PathVariable Long id, Model model) {
        Lugar lugar = lugarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lugar no encontrado"));
        model.addAttribute("lugar", lugar);
        return "moderador/l_gestion/ver";
    }

    @GetMapping("/lugares/{id}/editar")
    public String editarLugar(@PathVariable Long id, Model model) {
        Lugar lugar = lugarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lugar no encontrado"));

        model.addAttribute("lugar", lugar);
        model.addAttribute("tipos", TipoLugar.values());
        model.addAttribute("estados", EstadoLugar.values());

        return "moderador/l_gestion/editar";
    }

    @PostMapping("/lugares/{id}/editar")
    public String actualizarLugar(
            @PathVariable Long id,
            @ModelAttribute Lugar datos) {

        Lugar lugar = lugarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lugar no encontrado"));

        lugar.setNombre(datos.getNombre());
        lugar.setTipoL(datos.getTipoL());
        lugar.setDescripcion(datos.getDescripcion());
        lugar.setCiudad(datos.getCiudad());
        lugar.setDireccion(datos.getDireccion());
        lugar.setEstado(datos.getEstado());

        lugarRepository.save(lugar);

        return "redirect:/moderador/lugares";
    }

    // ============================================================
    // GESTIÓN DE EVENTOS
    // ============================================================
    @GetMapping("/eventos")
    public String eventos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tipo,
            Model model) {

        List<Evento> eventos = eventoRepository.findAll();

        eventos = filtrarEventos(eventos, search, tipo);

        model.addAttribute("listaEventos", eventos);
        model.addAttribute("search", search);
        model.addAttribute("tipo", tipo);
        model.addAttribute("tipos", TipoEvento.values());

        return "moderador/ev_gestion/listar";
    }

    @GetMapping("/eventos/crear")
    public String crearEvento(Model model) {
        model.addAttribute("evento", new Evento());
        model.addAttribute("tipos", TipoEvento.values());
        model.addAttribute("lugares", lugarRepository.findAll());
        return "moderador/ev_gestion/crear";
    }

    @PostMapping("/eventos/crear")
    public String guardarEvento(@ModelAttribute Evento evento) {
        eventoRepository.save(evento);
        return "redirect:/moderador/eventos";
    }

    @GetMapping("/eventos/{id}")
    public String verEvento(@PathVariable Long id, Model model) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        model.addAttribute("evento", evento);
        return "moderador/ev_gestion/ver";
    }

    @GetMapping("/eventos/{id}/editar")
    public String editarEvento(@PathVariable Long id, Model model) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        model.addAttribute("evento", evento);
        model.addAttribute("tipos", TipoEvento.values());
        model.addAttribute("lugares", lugarRepository.findAll());

        return "moderador/ev_gestion/editar";
    }

    @PostMapping("/eventos/{id}/editar")
    public String actualizarEvento(
            @PathVariable Long id,
            @ModelAttribute Evento datos) {

        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        evento.setTitulo(datos.getTitulo());
        evento.setDescripcion(datos.getDescripcion());
        evento.setTipoEvento(datos.getTipoEvento());
        evento.setFechaInicio(datos.getFechaInicio());
        evento.setFechaFin(datos.getFechaFin());
        evento.setLugar(datos.getLugar());

        eventoRepository.save(evento);

        return "redirect:/moderador/eventos";
    }

    // ============================================================
    // GESTIÓN DE ESTABLECIMIENTOS SOLO PENDIENTES
    // ============================================================
    @GetMapping("/establecimientos")
    public String establecimientosPendientes(Model model) {
        List<Establecimiento> pendientes = establecimientoRepository.findByEstado(EstadoEstablecimiento.PENDIENTE);

        model.addAttribute("pendientes", pendientes);
        return "moderador/e_gestion/listar";
    }

    @PostMapping("/establecimientos/{id}/aprobar")
    public String aprobarEstablecimiento(@PathVariable Long id) {
        establecimientoRepository.findById(id).ifPresent(e -> {
            e.setEstado(EstadoEstablecimiento.APROBADO);
            establecimientoRepository.save(e);
        });
        return "redirect:/moderador/establecimientos";
    }

    @PostMapping("/establecimientos/{id}/rechazar")
    public String rechazarEstablecimiento(@PathVariable Long id) {
        establecimientoRepository.findById(id).ifPresent(e -> {
            e.setEstado(EstadoEstablecimiento.RECHAZADO);
            establecimientoRepository.save(e);
        });
        return "redirect:/moderador/establecimientos";
    }

    // ============================================================
    // MÉTODOS PRIVADOS PARA FILTRAR LISTADOS
    // ============================================================
    private List<Lugar> filtrarLugares(
            List<Lugar> lugares,
            String search,
            String tipo,
            String estado) {

        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            lugares = lugares.stream()
                    .filter(l -> l.getNombre().toLowerCase().contains(q) ||
                            (l.getDireccion() != null && l.getDireccion().toLowerCase().contains(q)))
                    .collect(Collectors.toList());
        }

        if (tipo != null && !tipo.isBlank()) {
            lugares = lugares.stream()
                    .filter(l -> l.getTipoL() != null && l.getTipoL().name().equals(tipo))
                    .collect(Collectors.toList());
        }

        if (estado != null && !estado.isBlank()) {
            lugares = lugares.stream()
                    .filter(l -> l.getEstado() != null && l.getEstado().name().equals(estado))
                    .collect(Collectors.toList());
        }

        return lugares;
    }

    private List<Evento> filtrarEventos(
            List<Evento> eventos,
            String search,
            String tipo) {

        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            eventos = eventos.stream()
                    .filter(e -> e.getTitulo().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        if (tipo != null && !tipo.isBlank()) {
            eventos = eventos.stream()
                    .filter(e -> e.getTipoEvento() != null && e.getTipoEvento().name().equals(tipo))
                    .collect(Collectors.toList());
        }

        return eventos;
    }
}