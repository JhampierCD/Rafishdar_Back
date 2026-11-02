package pe.edu.upc.rafishdar_back.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bitacoras")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Bitacora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitudPesca;
    private Double longitudPesca;
    private LocalDateTime fechaHoraRegistro;
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "faena_id")
    private Faena faena;

    @ManyToOne
    @JoinColumn(name = "zona_pesca_id")
    private ZonaPesca zonaPesca;

    @JsonIgnore
    @OneToMany(mappedBy = "bitacora", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<DetalleCaptura> detallesCaptura;

    @OneToOne(mappedBy = "bitacora")
    @JsonIgnore
    private CondicionClimatica condicionClimatica;
}
