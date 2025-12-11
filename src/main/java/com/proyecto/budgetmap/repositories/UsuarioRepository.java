package com.proyecto.budgetmap.repositories;

import com.proyecto.budgetmap.models.Usuario;
import com.proyecto.budgetmap.models.enums.EstadoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByEstado(EstadoUsuario estado);
}
