package com.proyecto.budgetmap.security;

import com.proyecto.budgetmap.models.Usuario;
import com.proyecto.budgetmap.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // BLOQUEAR login si el usuario NO está activo
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new UsernameNotFoundException("Tu cuenta está pendiente de aprobación o inactiva.");
        }

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities("ROLE_" + usuario.getRol().name())
                .build();
    }

}
