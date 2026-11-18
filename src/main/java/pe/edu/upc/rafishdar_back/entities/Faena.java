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

public class Faena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHoraSalida;
    private LocalDateTime fechaHoraLlegada;
    private String estado; // Programada, En curso, Finalizada, Cancelada

    @OneToMany(mappedBy = "faena", fetch = FetchType.EAGER)
    private List<Bitacora> bitacoras;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "embarcacion_id")
    private Embarcacion embarcacion;

    @OneToOne(mappedBy = "faena")
    @JsonIgnore
    private GastoOperativo gastoOperativo;

    @OneToOne(mappedBy = "faena")
    @JsonIgnore
    private RecomendacionIA recomendacionIA;
}
