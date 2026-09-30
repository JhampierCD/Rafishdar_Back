package pe.edu.upc.rafishdar_back.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditorias")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accion;
    private String tablaAfectada;
    private LocalDateTime fechaHora;
    private String detalles;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;
}
