package com.proyecto.budgetmap.models;

import com.proyecto.budgetmap.models.enums.EstadoUsuario;
import com.proyecto.budgetmap.models.enums.Rol;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    private String nit;
    private String razonSocial;

    private String nombre;
    private String apellido;
    private String direccion;

    private String horario;
    private String telefono;

    private String rutPath; // Ruta al archivo RUT almacenado

    @Column(unique = true, nullable = false)
    private String email; // LOGIN

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoUsuario estado = EstadoUsuario.PENDIENTE;
}
