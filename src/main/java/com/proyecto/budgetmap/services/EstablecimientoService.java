package com.proyecto.budgetmap.services;

import com.proyecto.budgetmap.models.Establecimiento;
import com.proyecto.budgetmap.repositories.EstablecimientoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstablecimientoService {

    private final EstablecimientoRepository establecimientoRepository;

    public EstablecimientoService(EstablecimientoRepository establecimientoRepository) {
        this.establecimientoRepository = establecimientoRepository;
    }

    public Establecimiento crear(Establecimiento establecimiento) {
        return establecimientoRepository.save(establecimiento);
    }

    public List<Establecimiento> listar() {
        return establecimientoRepository.findAll();
    }

    public Optional<Establecimiento> buscarPorId(Long id) {
        return establecimientoRepository.findById(id);
    }

    public Establecimiento actualizar(Establecimiento establecimiento) {
        return establecimientoRepository.save(establecimiento);
    }

    public void eliminar(Long id) {
        establecimientoRepository.deleteById(id);
    }
}