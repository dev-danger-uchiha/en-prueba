package com.proyecto.budgetmap.services;

import com.proyecto.budgetmap.models.Reserva;
import com.proyecto.budgetmap.repositories.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public Reserva crear(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public Reserva actualizar(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }
}
