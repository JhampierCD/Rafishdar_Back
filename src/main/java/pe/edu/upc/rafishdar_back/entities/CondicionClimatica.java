package pe.edu.upc.rafishdar_back.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "condiciones_climaticas")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CondicionClimatica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Double temperaturaCelsius;
    private Double velocidadVientoNudos;
    private String estadoOleaje;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "bitacora_id")
    private BitacoraFaena bitacora;

}