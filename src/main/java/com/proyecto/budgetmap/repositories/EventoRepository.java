package com.proyecto.budgetmap.repositories;

import com.proyecto.budgetmap.models.Evento;
import com.proyecto.budgetmap.models.Lugar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByLugar(Lugar lugar);
}
