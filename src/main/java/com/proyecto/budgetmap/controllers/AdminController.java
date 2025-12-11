package com.proyecto.budgetmap.controllers;

import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public AdminController(
            UsuarioRepository usuarioRepository,
            EstablecimientoRepository establecimientoRepository,
            LugarRepository lugarRepository,
            EventoRepository eventoRepository,
            PqrsRepository pqrsRepository,
            ReservaRepository reservaRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.establecimientoRepository = establecimientoRepository;
        this.lugarRepository = lugarRepository;
        this.eventoRepository = eventoRepository;
        this.pqrsRepository = pqrsRepository;
        this.reservaRepository = reservaRepository;
        this.passwordEncoder = passwordEncoder;
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

        model.addAttribute("establecimientosPendientes",
                establecimientoRepository.countByEstado(EstadoEstablecimiento.PENDIENTE));
        model.addAttribute("establecimientosAprobados",
                establecimientoRepository.countByEstado(EstadoEstablecimiento.APROBADO));
        model.addAttribute("establecimientosRechazados",
                establecimientoRepository.countByEstado(EstadoEstablecimiento.RECHAZADO));

        model.addAttribute("lugaresBorradores",
                lugarRepository.countByEstado(EstadoLugar.BORRADOR));
        model.addAttribute("lugaresPublicados",
                lugarRepository.countByEstado(EstadoLugar.PUBLICADO));

        model.addAttribute("eventosPendientes",
                eventoRepository.countByEstado(EstadoEvento.PENDIENTE));
        model.addAttribute("eventosActivos",
                eventoRepository.countByEstado(EstadoEvento.ACTIVO));
        model.addAttribute("eventosRechazados",
                eventoRepository.countByEstado(EstadoEvento.RECHAZADO));
        model.addAttribute("eventosFinalizados",
                eventoRepository.countByEstado(EstadoEvento.FINALIZADO));

        // model.addAttribute("PQRSPendientes",
        // pqrsRepository.countByEstado(EstadoPqrs.PENDIENTE));
        // model.addAttribute("PQRSActivas",
        // pqrsRepository.countByEstado(EstadoPqrs.ACTIVO));
        // model.addAttribute("PQRSCerradas",
        // pqrsRepository.countByEstado(EstadoPqrs.CERRADA));

        return "admin/dashboard";
    }

    @GetMapping("/usuarios")
    public String usuarios(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String estado,
            Model model) {

        List<Usuario> usuarios = usuarioRepository.findAll();

        usuarios = filtrarUsuarios(usuarios, search, rol, estado);

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
        usuarios = filtrarUsuarios(usuarios, search, rol, estado);

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
    public String actualizarUsuario(@PathVariable Long id,
            @ModelAttribute("usuario") Usuario datos) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(datos.getNombre());
        usuario.setEmail(datos.getEmail());
        usuario.setRol(datos.getRol());
        usuario.setEstado(datos.getEstado());

        // NO tocar contraseña, no editar, no reemplazar
        // usuario.setPassword(...);

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
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);

        return "redirect:/admin/usuarios";
    }

    // MÉTODO PRIVADO PARA REUTILIZAR LA LÓGICA DE FILTRADO
    private List<Usuario> filtrarUsuarios(List<Usuario> usuarios, String search, String rol, String estado) {
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
    /////////////////////////////////////////////////////////
    // GESTIÓN DE ESTABLECIMIENTOS
    /////////////////////////////////////////////////////////

    @GetMapping("/establecimientos")
    public String establecimientos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            Model model) {

        List<Establecimiento> lista = establecimientoRepository.findAll();

        lista = filtrarEstablecimientos(lista, search, tipo, estado);

        model.addAttribute("listaEstablecimientos", lista);
        model.addAttribute("search", search);
        model.addAttribute("tipo", tipo);
        model.addAttribute("estado", estado);

        return "admin/e_gestion/listar";
    }

    @GetMapping("/establecimientos/crear")
    public String crearEstablecimientoForm(Model model) {
        model.addAttribute("establecimiento", new Establecimiento());
        return "admin/e_gestion/crear";
    }

    @PostMapping("/establecimientos/crear")
    public String crearEstablecimiento(@ModelAttribute Establecimiento establecimiento) {
        establecimiento.setEstado(EstadoEstablecimiento.PENDIENTE);
        establecimientoRepository.save(establecimiento);
        return "redirect:/admin/establecimientos";
    }

    @GetMapping("/establecimientos/{id}")
    public String verEstablecimiento(@PathVariable Long id, Model model) {
        Establecimiento e = establecimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado"));
        model.addAttribute("establecimiento", e);
        return "admin/e_gestion/ver";
    }

    @GetMapping("/establecimientos/{id}/editar")
    public String editarEstablecimiento(@PathVariable Long id, Model model) {
        Establecimiento e = establecimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado"));
        model.addAttribute("establecimiento", e);
        return "admin/e_gestion/editar";
    }

    @PostMapping("/establecimientos/{id}/editar")
    public String actualizarEstablecimiento(
            @PathVariable Long id,
            @ModelAttribute Establecimiento datos) {

        Establecimiento e = establecimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado"));

        e.setRazonSocial(datos.getRazonSocial());
        e.setNit(datos.getNit());
        e.setDireccion(datos.getDireccion());
        e.setTelefono(datos.getTelefono());
        e.setRutPdfUrl(datos.getRutPdfUrl());
        e.setTipoE(datos.getTipoE());
        e.setDescripcion(datos.getDescripcion());
        e.setHorarios(datos.getHorarios());
        e.setEstado(datos.getEstado());

        establecimientoRepository.save(e);

        return "redirect:/admin/establecimientos";
    }

    @PostMapping("/establecimientos/{id}/eliminar")
    public String eliminarEstablecimiento(@PathVariable Long id) {
        establecimientoRepository.deleteById(id);
        return "redirect:/admin/establecimientos";
    }

    @PostMapping("/establecimientos/{id}/aprobar")
    public String aprobarEstablecimiento(@PathVariable Long id) {
        establecimientoRepository.findById(id).ifPresent(e -> {
            e.setEstado(EstadoEstablecimiento.APROBADO);
            establecimientoRepository.save(e);
        });
        return "redirect:/admin/establecimientos";
    }

    @PostMapping("/establecimientos/{id}/rechazar")
    public String rechazarEstablecimiento(@PathVariable Long id) {
        establecimientoRepository.findById(id).ifPresent(e -> {
            e.setEstado(EstadoEstablecimiento.RECHAZADO);
            establecimientoRepository.save(e);
        });
        return "redirect:/admin/establecimientos";
    }

    /////////////////////////////////////////////////////////
    // MÉTODO PRIVADO PARA FILTRADO
    /////////////////////////////////////////////////////////
    @GetMapping("/establecimientos/exportar-pdf")
    public void exportarPdfEstablecimientos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=establecimientos.pdf");

        List<Establecimiento> establecimientos = establecimientoRepository.findAll();
        establecimientos = filtrarEstablecimientos(establecimientos, search, tipo, estado);

        com.itextpdf.text.Document pdf = new com.itextpdf.text.Document();
        com.itextpdf.text.pdf.PdfWriter.getInstance(pdf, response.getOutputStream());

        pdf.open();

        pdf.add(new com.itextpdf.text.Paragraph("Lista de Establecimientos"));
        pdf.add(new com.itextpdf.text.Paragraph(" "));

        com.itextpdf.text.pdf.PdfPTable tabla = new com.itextpdf.text.pdf.PdfPTable(4);
        tabla.addCell("Razón Social");
        tabla.addCell("NIT");
        tabla.addCell("Dirección");
        tabla.addCell("Estado");

        for (Establecimiento e : establecimientos) {
            tabla.addCell(e.getRazonSocial() == null ? "" : e.getRazonSocial());
            tabla.addCell(e.getNit() == null ? "" : e.getNit());
            tabla.addCell(e.getDireccion() == null ? "" : e.getDireccion());
            tabla.addCell(e.getEstado() == null ? "" : e.getEstado().toString());
        }

        pdf.add(tabla);
        pdf.close();
    }

    private List<Establecimiento> filtrarEstablecimientos(
            List<Establecimiento> lista,
            String search,
            String tipo,
            String estado) {

        String q = (search == null ? "" : search).trim().toLowerCase();

        if (!q.isEmpty()) {
            lista = lista.stream()
                    .filter(e -> (e.getRazonSocial() != null && e.getRazonSocial().toLowerCase().contains(q)))
                    .toList();
        }

        if (tipo != null && !tipo.isEmpty()) {
            lista = lista.stream()
                    .filter(e -> e.getTipoE() != null && e.getTipoE().name().equals(tipo))
                    .toList();
        }

        if (estado != null && !estado.isEmpty()) {
            lista = lista.stream()
                    .filter(e -> e.getEstado() != null && e.getEstado().toString().equals(estado))
                    .toList();
        }

        return lista;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////// LUGARES/////////////////////////////

    // ---------------------------------------------
    // GESTIÓN DE LUGARES (CRUD COMPLETO)
    // ---------------------------------------------

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

        return "admin/l_gestion/listar";
    }

    @GetMapping("/lugares/crear")
    public String crearLugarForm(Model model) {
        model.addAttribute("lugar", new Lugar());
        model.addAttribute("tipos", TipoLugar.values());
        return "admin/l_gestion/crear";
    }

    @PostMapping("/lugares/crear")
    public String crearLugar(@ModelAttribute Lugar lugar) {
        lugar.setEstado(EstadoLugar.BORRADOR); // o el que tú uses por defecto
        lugarRepository.save(lugar);
        return "redirect:/admin/lugares";
    }

    @GetMapping("/lugares/{id}")
    public String verLugar(@PathVariable Long id, Model model) {
        Lugar lugar = lugarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lugar no encontrado"));
        model.addAttribute("lugar", lugar);
        return "admin/l_gestion/ver";
    }

    @GetMapping("/lugares/{id}/editar")
    public String editarLugar(@PathVariable Long id, Model model) {
        Lugar lugar = lugarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lugar no encontrado"));

        model.addAttribute("lugar", lugar);
        model.addAttribute("tipos", TipoLugar.values());
        model.addAttribute("estados", EstadoLugar.values());
        return "admin/l_gestion/editar";
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

        return "redirect:/admin/lugares";
    }

    @PostMapping("/lugares/{id}/eliminar")
    public String eliminarLugar(@PathVariable Long id) {
        if (!lugarRepository.existsById(id)) {
            throw new RuntimeException("Lugar no encontrado");
        }
        lugarRepository.deleteById(id);
        return "redirect:/admin/lugares";
    }

    @PostMapping("/lugares/{id}/publicar")
    public String publicarLugar(@PathVariable Long id) {
        lugarRepository.findById(id).ifPresent(lugar -> {
            if (lugar.getEstado() == EstadoLugar.BORRADOR) {
                lugar.setEstado(EstadoLugar.PUBLICADO);
                lugarRepository.save(lugar);
            }
        });
        return "redirect:/admin/lugares";
    }

    @PostMapping("/lugares/{id}/borrador")
    public String borradorLugar(@PathVariable Long id) {
        lugarRepository.findById(id).ifPresent(lugar -> {
            if (lugar.getEstado() == EstadoLugar.PUBLICADO) {
                lugar.setEstado(EstadoLugar.BORRADOR);
                lugarRepository.save(lugar);
            }
        });
        return "redirect:/admin/lugares";
    }

    // ---------------------------------------------
    // FILTRO PRIVADO PARA LUGARES
    // ---------------------------------------------
    private List<Lugar> filtrarLugares(
            List<Lugar> lugares,
            String search,
            String tipo,
            String estado) {

        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            lugares = lugares.stream()
                    .filter(l -> (l.getNombre() != null && l.getNombre().toLowerCase().contains(q)) ||
                            (l.getDireccion() != null && l.getDireccion().toLowerCase().contains(q)))
                    .toList();
        }

        if (tipo != null && !tipo.isBlank()) {
            lugares = lugares.stream()
                    .filter(l -> l.getTipoL() != null && l.getTipoL().name().equals(tipo))
                    .toList();
        }

        if (estado != null && !estado.isBlank()) {
            lugares = lugares.stream()
                    .filter(l -> l.getEstado() != null && l.getEstado().name().equals(estado))
                    .toList();
        }

        return lugares;

    }
}