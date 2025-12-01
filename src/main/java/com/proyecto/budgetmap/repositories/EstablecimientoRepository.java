package com.proyecto.budgetmap.repositories;

import com.proyecto.budgetmap.models.Establecimiento;
import com.proyecto.budgetmap.models.enums.EstadoEstablecimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstablecimientoRepository extends JpaRepository<Establecimiento, Long> {
    long countByEstado(EstadoEstablecimiento estado);
}
