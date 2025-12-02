package com.proyecto.budgetmap.controllers;

import com.proyecto.budgetmap.models.Usuario;
import com.proyecto.budgetmap.models.enums.EstadoUsuario;
import com.proyecto.budgetmap.models.enums.Rol;
import com.proyecto.budgetmap.services.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // LOGIN
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    // REGISTRO SELECCIÓN
    @GetMapping("/registro")
    public String registroSeleccion() {
        return "auth/registro";
    }

    // REGISTRO CLIENTE
    @GetMapping("/registro/cliente")
    public String registroClienteForm() {
        return "auth/registro_cliente";
    }

    @PostMapping("/registro/cliente")
    public String procesarRegistroCliente(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String telefono,
            @RequestParam String direccion,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setApellido(apellido);
        nuevo.setTelefono(telefono);
        nuevo.setDireccion(direccion);
        nuevo.setEmail(email);
        nuevo.setPassword(password);
        nuevo.setRol(Rol.CLIENTE);
        nuevo.setEstado(EstadoUsuario.ACTIVO); // ⭐ cliente activo por defecto

        usuarioService.registrar(nuevo);

        model.addAttribute("mensaje", "Cliente registrado correctamente");
        return "auth/login";
    }

    // REGISTRO MODERADOR
    @GetMapping("/registro/moderador")
    public String registroModeradorForm() {
        return "auth/registro_moderador";
    }

    @PostMapping("/registro/moderador")
    public String procesarRegistroModerador(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String direccion,
            @RequestParam String telefono,
            @RequestParam String email,
            @RequestParam String password,

            Model model) {

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setApellido(apellido);
        nuevo.setDireccion(direccion);
        nuevo.setTelefono(telefono);
        nuevo.setEmail(email);
        nuevo.setPassword(password);
        nuevo.setRol(Rol.MODERADOR);
        nuevo.setEstado(EstadoUsuario.PENDIENTE); // ⭐ moderador pendiente de verificación

        usuarioService.registrar(nuevo);

        model.addAttribute("mensaje",
                "Moderador registrado. Tu cuenta será verificada antes de activarse.");

        return "auth/login";
    }

    // REGISTRO PROPIETARIO ESTABLECIMIENTO
    @GetMapping("/registro/establecimiento")
    public String registroEstablecimientoForm() {
        return "auth/registro_establecimiento";
    }

    @PostMapping("/registro/establecimiento")
    public String procesarRegistroEstablecimiento(
            @RequestParam String nit,
            @RequestParam String razonSocial,
            @RequestParam String direccion,
            @RequestParam String telefono,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String horario,

            @RequestParam("rutFile") MultipartFile rutPDF,
            Model model) {

        Usuario nuevo = new Usuario();
        nuevo.setNit(nit);
        nuevo.setRazonSocial(razonSocial);
        nuevo.setDireccion(direccion);
        nuevo.setTelefono(telefono);
        nuevo.setEmail(email);
        nuevo.setPassword(password);
        nuevo.setHorario(horario);
        nuevo.setRol(Rol.PROPIETARIO_ESTABLECIMIENTO);
        nuevo.setEstado(EstadoUsuario.PENDIENTE); // ⭐ establecimiento pendiente de verificación

        nuevo.setRutPath("pendiente");

        usuarioService.registrar(nuevo);

        model.addAttribute("mensaje",
                "PropietarioEstablecimiento registrado. Tu cuenta será verificada antes de activarse.");

        return "auth/login";
    }
}
