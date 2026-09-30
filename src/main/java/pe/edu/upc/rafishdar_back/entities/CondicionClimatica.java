package pe.edu.upc.rafishdar_back.entities;

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

    @OneToOne
    @JoinColumn(name = "bitacora_id")
    private BitacoraFaena bitacora;

}