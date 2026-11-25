package pe.edu.upc.rafishdar_back.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String nombreCientifico;
    private Boolean estadoVeda;

    @JsonIgnore
    @OneToMany(mappedBy = "especie")
    private List<DetalleCaptura> detallesCaptura;

    @JsonIgnore
    @OneToMany(mappedBy = "especie")
    private List<CuotaPesca> cuotasPesca;

}