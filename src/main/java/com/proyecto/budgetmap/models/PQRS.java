package com.proyecto.budgetmap.models;

import com.proyecto.budgetmap.models.enums.EstadoPQRS;
import com.proyecto.budgetmap.models.enums.TipoPQRS;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PQRS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoPQRS tipo;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Enumerated(EnumType.STRING)
    private EstadoPQRS estado = EstadoPQRS.ABIERTA;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "asignado_a")
    private Usuario asignadoA; // admin/moderador
}
