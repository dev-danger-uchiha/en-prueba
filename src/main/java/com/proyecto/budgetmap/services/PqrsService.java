package com.proyecto.budgetmap.services;

import com.proyecto.budgetmap.models.PQRS;
import com.proyecto.budgetmap.repositories.PqrsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PqrsService {

    private final PqrsRepository pqrsRepository;

    public PqrsService(PqrsRepository pqrsRepository) {
        this.pqrsRepository = pqrsRepository;
    }

    public PQRS crear(PQRS pqrs) {
        return pqrsRepository.save(pqrs);
    }

    public PQRS actualizar(PQRS pqrs) {
        return pqrsRepository.save(pqrs);
    }

    public Optional<PQRS> buscarPorId(Long id) {
        return pqrsRepository.findById(id);
    }

    public List<PQRS> listar() {
        return pqrsRepository.findAll();
    }

    public void eliminar(Long id) {
        pqrsRepository.deleteById(id);
    }
}
