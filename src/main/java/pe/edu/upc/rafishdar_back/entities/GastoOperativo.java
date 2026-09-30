package pe.edu.upc.rafishdar_back.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gastos_operativos")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class GastoOperativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double galonesCombustible;
    private Double costoCombustible;
    private Double costoHieloInsumos;

    @OneToOne
    @JoinColumn(name = "bitacora_id")
    private BitacoraFaena bitacora;

}
