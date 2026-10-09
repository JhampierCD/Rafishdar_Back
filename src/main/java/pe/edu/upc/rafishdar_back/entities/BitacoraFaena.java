package pe.edu.upc.rafishdar_back.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bitacoras_faena")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class BitacoraFaena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHoraSalida;
    private LocalDateTime fechaHoraLlegada;
    private String estado;
    private String observaciones;

    private Double latitudPesca;
    private Double longitudPesca;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "embarcacion_id")
    private Embarcacion embarcacion;

    @ManyToOne
    @JoinColumn(name = "zona_pesca_id")
    private ZonaPesca zonaPesca;

    @JsonIgnore
    @OneToMany(mappedBy = "bitacora", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<DetalleCaptura> detallesCaptura;

    @OneToOne(mappedBy = "bitacora")
    private GastoOperativo gastoOperativo;

    @OneToOne(mappedBy = "bitacora")
    @JsonIgnore
    private CondicionClimatica condicionClimatica;

    @OneToOne(mappedBy = "bitacora")
    @JsonIgnore
    private RecomendacionIA recomendacionIA;

}
