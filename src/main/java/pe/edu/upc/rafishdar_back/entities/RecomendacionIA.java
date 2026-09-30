package pe.edu.upc.rafishdar_back.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recomendaciones_ia")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class RecomendacionIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String analisisTexto;
    private String nivelRiesgo;
    private LocalDateTime fechaGeneracion;

    @OneToOne
    @JoinColumn(name = "bitacora_id")
    private BitacoraFaena bitacora;

}

