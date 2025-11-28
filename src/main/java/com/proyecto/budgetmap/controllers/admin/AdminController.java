package com.proyecto.budgetmap.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyecto.budgetmap.models.Usuario;
import com.proyecto.budgetmap.models.enums.EstadoUsuario;
import com.proyecto.budgetmap.repositories.UsuarioRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioRepository usuarioRepository;

    public AdminController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // DASHBOARD PRINCIPAL
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("usuariosPendientes",
                usuarioRepository.countByEstado(EstadoUsuario.PENDIENTE));

        model.addAttribute("usuariosActivos",
                usuarioRepository.countByEstado(EstadoUsuario.ACTIVO));

        return "admin/dashboard";
    }

    // LISTA DE USUARIOS
    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("listaUsuarios", usuarioRepository.findAll());
        return "admin/usuarios";
    }

    // VER USUARIO
    @GetMapping("/usuarios/{id}")
    public String verUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        return "admin/ver-usuario";
    }

    // APROBAR USUARIO
    @PostMapping("/usuarios/{id}/aprobar")
    public String aprobarUsuario(@PathVariable Long id) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            if (usuario.getEstado() == EstadoUsuario.PENDIENTE) {
                usuario.setEstado(EstadoUsuario.ACTIVO);
                usuarioRepository.save(usuario);
            }
        });
        return "redirect:/admin/usuarios";
    }

    // RECHAZAR USUARIO
    @PostMapping("/usuarios/{id}/rechazar")
    public String rechazarUsuario(@PathVariable Long id) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            if (usuario.getEstado() == EstadoUsuario.PENDIENTE) {
                usuario.setEstado(EstadoUsuario.RECHAZADO);
                usuarioRepository.save(usuario);
            }
        });
        return "redirect:/admin/usuarios";
    }
}