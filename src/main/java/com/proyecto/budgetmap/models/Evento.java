package com.proyecto.budgetmap.models;

import com.proyecto.budgetmap.models.enums.TipoEvento;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "lugar_id")
    private Lugar lugar;

    @ManyToOne
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;
}
