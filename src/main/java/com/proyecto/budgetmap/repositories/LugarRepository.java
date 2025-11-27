package com.proyecto.budgetmap.repositories;

import com.proyecto.budgetmap.models.Lugar;
import com.proyecto.budgetmap.models.enums.EstadoLugar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LugarRepository extends JpaRepository<Lugar, Long> {
    List<Lugar> findByEstado(EstadoLugar estado);
}