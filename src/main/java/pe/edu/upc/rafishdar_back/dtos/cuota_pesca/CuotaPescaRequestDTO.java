package pe.edu.upc.rafishdar_back.dtos.cuota_pesca;

import lombok.Data;

@Data
public class CuotaPescaRequestDTO {
    private Double limiteToneladasIndustrial;
    private Long especieId;
    private Long temporadaId;
}
