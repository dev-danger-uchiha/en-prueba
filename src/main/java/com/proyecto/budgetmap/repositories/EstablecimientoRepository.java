package com.proyecto.budgetmap.repositories;

import com.proyecto.budgetmap.models.Establecimiento;
import com.proyecto.budgetmap.models.Usuario;
import com.proyecto.budgetmap.models.enums.EstadoEstablecimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstablecimientoRepository extends JpaRepository<Establecimiento, Long> {

    long countByEstado(EstadoEstablecimiento estado);

    List<Establecimiento> findByEstado(EstadoEstablecimiento estado);

    List<Establecimiento> findByUsuario(Usuario usuario);
}