package pe.edu.upc.rafishdar_back.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "zonas_pesca")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ZonaPesca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreZona;
    private Double latitud;
    private Double longitud;
    private Double distanciaCostaKm;
    private Double radioAreaKm;

    @OneToMany(mappedBy = "zonaPesca")
    private List<BitacoraFaena> bitacoras;
}