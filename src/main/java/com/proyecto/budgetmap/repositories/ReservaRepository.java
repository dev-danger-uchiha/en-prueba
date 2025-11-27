package com.proyecto.budgetmap.repositories;

import com.proyecto.budgetmap.models.Reserva;
import com.proyecto.budgetmap.models.Usuario;
import com.proyecto.budgetmap.models.Establecimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuario(Usuario usuario);

    List<Reserva> findByEstablecimiento(Establecimiento establecimiento);
}
