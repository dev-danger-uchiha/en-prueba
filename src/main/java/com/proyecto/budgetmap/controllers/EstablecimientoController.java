package com.proyecto.budgetmap.controllers;

import com.proyecto.budgetmap.models.Establecimiento;
import com.proyecto.budgetmap.models.Reserva;
import com.proyecto.budgetmap.models.Usuario;
import com.proyecto.budgetmap.models.enums.EstadoEstablecimiento;
import com.proyecto.budgetmap.repositories.EstablecimientoRepository;
import com.proyecto.budgetmap.repositories.ReservaRepository;
import com.proyecto.budgetmap.repositories.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/establecimiento")
public class EstablecimientoController {

    private final EstablecimientoRepository establecimientoRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;

    public EstablecimientoController(
            EstablecimientoRepository establecimientoRepository,
            ReservaRepository reservaRepository,
            UsuarioRepository usuarioRepository) {
        this.establecimientoRepository = establecimientoRepository;
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // DASHBOARD
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {

        // Obtener usuario logueado
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar establecimientos del usuario
        List<Establecimiento> misEstablecimientos = establecimientoRepository.findByUsuario(usuario);

        model.addAttribute("misEstablecimientos", misEstablecimientos);
        model.addAttribute("usuario", usuario);

        // Si tiene establecimiento aprobado, mostrar reservas
        if (!misEstablecimientos.isEmpty()) {
            Establecimiento miEstablecimiento = misEstablecimientos.get(0);
            List<Reserva> reservas = reservaRepository.findByEstablecimiento(miEstablecimiento);
            model.addAttribute("reservas", reservas);
        }

        return "establecimiento/dashboard";
    }

    // VER MI ESTABLECIMIENTO
    @GetMapping("/perfil")
    public String verPerfil(Authentication auth, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Establecimiento> misEstablecimientos = establecimientoRepository.findByUsuario(usuario);

        if (!misEstablecimientos.isEmpty()) {
            model.addAttribute("establecimiento", misEstablecimientos.get(0));
        } else {
            model.addAttribute("establecimiento", new Establecimiento());
        }

        return "establecimiento/perfil";
    }

    // EDITAR MI ESTABLECIMIENTO
    @PostMapping("/perfil/editar")
    public String editarPerfil(
            @ModelAttribute Establecimiento datos,
            Authentication auth) {

        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Establecimiento> misEstablecimientos = establecimientoRepository.findByUsuario(usuario);

        Establecimiento establecimiento;

        if (misEstablecimientos.isEmpty()) {
            // Crear nuevo
            establecimiento = new Establecimiento();
            establecimiento.setUsuario(usuario);
            establecimiento.setEstado(EstadoEstablecimiento.PENDIENTE);
        } else {
            // Editar existente
            establecimiento = misEstablecimientos.get(0);
        }

        // Actualizar datos
        establecimiento.setRazonSocial(datos.getRazonSocial());
        establecimiento.setNit(datos.getNit());
        establecimiento.setDireccion(datos.getDireccion());
        establecimiento.setTelefono(datos.getTelefono());
        establecimiento.setTipoE(datos.getTipoE());
        establecimiento.setDescripcion(datos.getDescripcion());
        establecimiento.setHorarios(datos.getHorarios());

        establecimientoRepository.save(establecimiento);

        return "redirect:/establecimiento/perfil?success=true";
    }

    // GESTIONAR RESERVAS
    @GetMapping("/reservas")
    public String reservas(Authentication auth, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Establecimiento> misEstablecimientos = establecimientoRepository.findByUsuario(usuario);

        if (!misEstablecimientos.isEmpty()) {
            Establecimiento miEstablecimiento = misEstablecimientos.get(0);
            List<Reserva> reservas = reservaRepository.findByEstablecimiento(miEstablecimiento);
            model.addAttribute("reservas", reservas);
            model.addAttribute("establecimiento", miEstablecimiento);
        }

        return "establecimiento/reservas";
    }

    // CONFIRMAR RESERVA
    @PostMapping("/reservas/{id}/confirmar")
    public String confirmarReserva(@PathVariable Long id) {
        reservaRepository.findById(id).ifPresent(reserva -> {
            reserva.setEstado(com.proyecto.budgetmap.models.enums.EstadoReserva.CONFIRMADA);
            reservaRepository.save(reserva);
        });
        return "redirect:/establecimiento/reservas?confirmada=true";
    }

    // CANCELAR RESERVA
    @PostMapping("/reservas/{id}/cancelar")
    public String cancelarReserva(@PathVariable Long id) {
        reservaRepository.findById(id).ifPresent(reserva -> {
            reserva.setEstado(com.proyecto.budgetmap.models.enums.EstadoReserva.CANCELADA);
            reservaRepository.save(reserva);
        });
        return "redirect:/establecimiento/reservas?cancelada=true";
    }
}