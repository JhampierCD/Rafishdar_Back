package pe.edu.upc.rafishdar_back.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "temporadas")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Temporada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreTemporada;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String descripcion;

    @OneToMany(mappedBy = "temporada")
    private List<CuotaPesca> cuotasPesca;

}
