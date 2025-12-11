package com.proyecto.budgetmap.repositories;

import com.proyecto.budgetmap.models.PQRS;
import com.proyecto.budgetmap.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PqrsRepository extends JpaRepository<PQRS, Long> {
    List<PQRS> findByUsuario(Usuario usuario);

    List<PQRS> findByAsignadoA(Usuario usuario);
}
