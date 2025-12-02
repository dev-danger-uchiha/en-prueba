package com.proyecto.budgetmap.models;

import com.proyecto.budgetmap.models.enums.EstadoEstablecimiento;
import com.proyecto.budgetmap.models.enums.TipoEstablecimiento;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Establecimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String razonSocial;
    private String nit;
    private String direccion;
    private String telefono;
    private String rutPdfUrl;

    @Enumerated(EnumType.STRING)
    private TipoEstablecimiento tipoE = TipoEstablecimiento.RESTAURANTE;

    private String descripcion;
    private String horarios;

    @Enumerated(EnumType.STRING)
    private EstadoEstablecimiento estado = EstadoEstablecimiento.PENDIENTE;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
