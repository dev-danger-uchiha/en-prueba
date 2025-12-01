package com.proyecto.budgetmap.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.proyecto.budgetmap.models.*;
import com.proyecto.budgetmap.models.enums.*;
import com.proyecto.budgetmap.repositories.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final EstablecimientoRepository establecimientoRepository;
    private final LugarRepository lugarRepository;
    private final EventoRepository eventoRepository;
    private final PqrsRepository pqrsRepository;
    private final ReservaRepository reservaRepository;

    public AdminController(
            UsuarioRepository usuarioRepository,
            EstablecimientoRepository establecimientoRepository,
            LugarRepository lugarRepository,
            EventoRepository eventoRepository,
            PqrsRepository pqrsRepository,
            ReservaRepository reservaRepository) {

        this.usuarioRepository = usuarioRepository;
        this.establecimientoRepository = establecimientoRepository;
        this.lugarRepository = lugarRepository;
        this.eventoRepository = eventoRepository;
        this.pqrsRepository = pqrsRepository;
        this.reservaRepository = reservaRepository;
    }

    // DASHBOARD PRINCIPAL
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("usuariosPendientes",
                usuarioRepository.countByEstado(EstadoUsuario.PENDIENTE));
        model.addAttribute("usuariosActivos",
                usuarioRepository.countByEstado(EstadoUsuario.ACTIVO));
        model.addAttribute("usuariosRechazados",
                usuarioRepository.countByEstado(EstadoUsuario.RECHAZADO));

        // temporal

        model.addAttribute("establecimientosPendientes", 0);
        model.addAttribute("establecimientosActivos", 0);
        model.addAttribute("establecimientosRechazados", 0);

        model.addAttribute("lugaresPendientes", 0);
        model.addAttribute("lugaresActivos", 0);
        model.addAttribute("lugaresRechazados", 0);

        model.addAttribute("eventosPendientes", 0);
        model.addAttribute("eventosActivos", 0);
        model.addAttribute("eventosRechazados", 0);
        model.addAttribute("eventosFinalizados", 0);

        model.addAttribute("PQRSPendientes", 0);
        model.addAttribute("PQRSActivas", 0);
        model.addAttribute("PQRSCerradas", 0);

        return "admin/dashboard";
    }

    @GetMapping("/usuarios")
    public String usuarios(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String estado,
            Model model) {

        List<Usuario> usuarios = usuarioRepository.findAll();

        usuarios = aplicarFiltros(usuarios, search, rol, estado);

        model.addAttribute("listaUsuarios", usuarios);

        // opcional: pasar los valores al model (no es estrictamente necesario si usas
        // ${param.*} en la vista)
        model.addAttribute("search", search);
        model.addAttribute("rol", rol);
        model.addAttribute("estado", estado);

        return "admin/u_gestion/listar";
    }

    // Exportar PDF ahora recibe los mismos parámetros y exporta la lista filtrada
    @GetMapping("/usuarios/exportar-pdf")
    public void exportarPdf(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String estado,
            HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios.pdf");

        List<Usuario> usuarios = usuarioRepository.findAll();
        usuarios = aplicarFiltros(usuarios, search, rol, estado);

        // DOCUMENTO PDF (iText)
        com.itextpdf.text.Document pdf = new com.itextpdf.text.Document();
        com.itextpdf.text.pdf.PdfWriter.getInstance(pdf, response.getOutputStream());

        pdf.open();

        pdf.add(new com.itextpdf.text.Paragraph("Lista de Usuarios"));
        pdf.add(new com.itextpdf.text.Paragraph(" "));

        com.itextpdf.text.pdf.PdfPTable tabla = new com.itextpdf.text.pdf.PdfPTable(4);
        tabla.addCell("Nombre");
        tabla.addCell("Email");
        tabla.addCell("Rol");
        tabla.addCell("Estado");

        for (Usuario u : usuarios) {
            tabla.addCell(u.getNombre() == null ? "" : u.getNombre());
            tabla.addCell(u.getEmail() == null ? "" : u.getEmail());
            tabla.addCell(u.getRol() == null ? "" : u.getRol().toString());
            tabla.addCell(u.getEstado() == null ? "" : u.getEstado().toString());
        }

        pdf.add(tabla);
        pdf.close();
    }

    // VER USUARIO INDIVIDUAL
    @GetMapping("/usuarios/{id}")
    public String verUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        return "admin/u_gestion/ver";
    }

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

    @GetMapping("/usuarios/{id}/editar")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        return "admin/u_gestion/editar";
    }

    @PostMapping("/usuarios/{id}/editar")
    public String actualizarUsuario(
            @PathVariable Long id,
            @ModelAttribute("usuario") Usuario datos) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(datos.getNombre());
        usuario.setEmail(datos.getEmail());
        usuario.setRol(datos.getRol());
        usuario.setEstado(datos.getEstado());

        usuarioRepository.save(usuario);

        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id) {

        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }

        usuarioRepository.deleteById(id);

        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/crear")
    public String crearUsuarioForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "admin/u_gestion/crear";
    }

    @PostMapping("/usuarios/crear")
    public String crearUsuario(@ModelAttribute Usuario usuario) {
        usuarioRepository.save(usuario);
        return "redirect:/admin/usuarios";
    }

    // MÉTODO PRIVADO PARA REUTILIZAR LA LÓGICA DE FILTRADO
    private List<Usuario> aplicarFiltros(List<Usuario> usuarios, String search, String rol, String estado) {
        // Normaliza el término de búsqueda
        String q = (search == null ? "" : search).trim().toLowerCase();

        // Filtro por texto (nombre o email)
        if (!q.isEmpty()) {
            usuarios = usuarios.stream()
                    .filter(u -> {
                        String nombre = u.getNombre() == null ? "" : u.getNombre().trim().toLowerCase();
                        String email = u.getEmail() == null ? "" : u.getEmail().trim().toLowerCase();
                        return nombre.contains(q) || email.contains(q);
                    })
                    .collect(Collectors.toList());
        }

        // Filtro por rol (string exacto del enum)
        if (rol != null && !rol.isEmpty()) {
            usuarios = usuarios.stream()
                    .filter(u -> u.getRol() != null && u.getRol().toString().equals(rol))
                    .collect(Collectors.toList());
        }

        // Filtro por estado (string exacto del enum)
        if (estado != null && !estado.isEmpty()) {
            usuarios = usuarios.stream()
                    .filter(u -> u.getEstado() != null && u.getEstado().toString().equals(estado))
                    .collect(Collectors.toList());
        }

        return usuarios;
    }

}