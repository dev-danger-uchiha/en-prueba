package com.proyecto.budgetmap.repositories;

import com.proyecto.budgetmap.models.Lugar;
import com.proyecto.budgetmap.models.enums.EstadoLugar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LugarRepository extends JpaRepository<Lugar, Long> {

    long countByEstado(EstadoLugar estado);
}