package pe.edu.upc.rafishdar_back.dtos.cuota_pesca;

import lombok.Data;

@Data
public class CuotaPescaReqUpdDTO {
    private Double limiteToneladasIndustrial;
    private Long especieId;
    private Long temporadaId;
}
