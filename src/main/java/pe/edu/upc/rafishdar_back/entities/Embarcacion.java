package pe.edu.upc.rafishdar_back.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @JsonIgnore
    @OneToMany(mappedBy = "embarcacion")
    private List<BitacoraFaena> bitacoras;
}
