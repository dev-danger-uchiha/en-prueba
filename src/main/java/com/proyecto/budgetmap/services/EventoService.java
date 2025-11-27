package com.proyecto.budgetmap.services;

import com.proyecto.budgetmap.models.Evento;
import com.proyecto.budgetmap.repositories.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public Evento crear(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Evento actualizar(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Optional<Evento> buscarPorId(Long id) {
        return eventoRepository.findById(id);
    }

    public List<Evento> listar() {
        return eventoRepository.findAll();
    }

    public void eliminar(Long id) {
        eventoRepository.deleteById(id);
    }
}