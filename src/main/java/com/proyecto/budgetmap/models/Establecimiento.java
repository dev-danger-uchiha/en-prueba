package com.proyecto.budgetmap.models;

import com.proyecto.budgetmap.models.enums.EstadoEstablecimiento;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Establecimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreEstablecimiento;
    private String nit;
    private String direccion;
    private String telefono;
    private String rutPdfUrl;
    private String tipo;
    private String descripcion;
    private String ubicacion;
    private String contacto;
    private String horarios;

    @Enumerated(EnumType.STRING)
    private EstadoEstablecimiento estado = EstadoEstablecimiento.PENDIENTE;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
