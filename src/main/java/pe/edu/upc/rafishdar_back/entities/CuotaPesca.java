package pe.edu.upc.rafishdar_back.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cuotas_pesca")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CuotaPesca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double limiteToneladasIndustrial;

    @ManyToOne
    @JoinColumn(name = "especie_id")
    private EspeciePez especie;

    @ManyToOne
    @JoinColumn(name = "temporada_id")
    private Temporada temporada;
}
