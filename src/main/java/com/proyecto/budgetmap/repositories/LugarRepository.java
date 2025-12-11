package com.proyecto.budgetmap.repositories;

import com.proyecto.budgetmap.models.Lugar;
import com.proyecto.budgetmap.models.enums.EstadoLugar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LugarRepository extends JpaRepository<Lugar, Long> {

    // Contar lugares por estado (útil para dashboard)
    long countByEstado(EstadoLugar estado);

    // Listar lugares filtrando por estado
    List<Lugar> findByEstado(EstadoLugar estado);

    // Puedes agregar más filtros si es necesario
    List<Lugar> findByNombreContainingIgnoreCase(String nombre);
}
