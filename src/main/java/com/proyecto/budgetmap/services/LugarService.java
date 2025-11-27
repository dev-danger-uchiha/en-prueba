package com.proyecto.budgetmap.services;

import com.proyecto.budgetmap.models.Lugar;
import com.proyecto.budgetmap.repositories.LugarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LugarService {

    private final LugarRepository lugarRepository;

    public LugarService(LugarRepository lugarRepository) {
        this.lugarRepository = lugarRepository;
    }

    public Lugar crear(Lugar lugar) {
        return lugarRepository.save(lugar);
    }

    public Lugar actualizar(Lugar lugar) {
        return lugarRepository.save(lugar);
    }

    public Optional<Lugar> buscarPorId(Long id) {
        return lugarRepository.findById(id);
    }

    public List<Lugar> listar() {
        return lugarRepository.findAll();
    }

    public void eliminar(Long id) {
        lugarRepository.deleteById(id);
    }
}
