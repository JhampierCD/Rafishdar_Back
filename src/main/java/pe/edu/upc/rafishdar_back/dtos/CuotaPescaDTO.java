package pe.edu.upc.rafishdar_back.dtos;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upc.rafishdar_back.entities.EspeciePez;
import pe.edu.upc.rafishdar_back.entities.Temporada;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CuotaPescaDTO {

    private Long id;
    private Double limiteToneladasIndustrial;
    private Long especieId;
    private Long temporadaId;

}
