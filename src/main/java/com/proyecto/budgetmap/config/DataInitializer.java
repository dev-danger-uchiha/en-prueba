package com.proyecto.budgetmap.config;

import com.proyecto.budgetmap.models.Usuario;
import com.proyecto.budgetmap.models.enums.EstadoUsuario;
import com.proyecto.budgetmap.models.enums.Rol;
import com.proyecto.budgetmap.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
        return args -> {

            if (!usuarioRepository.existsByEmail("admin@admin.com")) {

                Usuario admin = new Usuario();

                admin.setNombre("Administrador");
                admin.setApellido("Sistema"); // <── agregado
                admin.setEmail("admin@admin.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRol(Rol.ADMIN);
                admin.setEstado(EstadoUsuario.ACTIVO);

                // Campos opcionales se dejan nulos
                admin.setDireccion(null);
                admin.setTelefono(null);
                admin.setNit(null);
                admin.setRazonSocial(null);
                admin.setHorario(null);
                admin.setRutPath(null);

                usuarioRepository.save(admin);

                System.out.println("ADMIN creado: admin@admin.com / admin123");
            }
        };
    }
}
