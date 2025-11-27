package com.proyecto.budgetmap.models;

import com.proyecto.budgetmap.models.enums.EstadoLugar;
import com.proyecto.budgetmap.models.enums.TipoLugar;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Lugar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoLugar tipoLugar;

    private String descripcion;
    private String ciudad;
    private String direccion;

    @Enumerated(EnumType.STRING)
    private EstadoLugar estado = EstadoLugar.BORRADOR;

    @ManyToOne
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;
}