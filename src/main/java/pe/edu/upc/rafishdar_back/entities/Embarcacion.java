package pe.edu.upc.rafishdar_back.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "embarcaciones")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Embarcacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String matricula;
    private Double capacidadToneladas;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @OneToMany(mappedBy = "embarcacion")
    private List<BitacoraFaena> bitacoras;
}
