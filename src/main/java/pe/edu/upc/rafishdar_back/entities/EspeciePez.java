package pe.edu.upc.rafishdar_back.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "especies_pez")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class EspeciePez {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreComun;
    private Boolean estadoVeda;

    @OneToMany(mappedBy = "especie")
    private List<DetalleCaptura> detallesCaptura;

    @OneToMany(mappedBy = "especie")
    private List<CuotaPesca> cuotasPesca;

}
