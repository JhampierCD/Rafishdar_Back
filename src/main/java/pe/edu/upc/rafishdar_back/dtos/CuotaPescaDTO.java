package pe.edu.upc.rafishdar_back.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class CuotaPescaDTO {

    private Long id;
    private Double limiteToneladasIndustrial;
    private Long especieId;
    private Long temporadaId;
    private EspeciePezDTO especie;

}
