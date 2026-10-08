package pe.edu.upc.rafishdar_back.dtos;

import lombok.Data;

@Data
public class GastoOperativoRequestDTO {
    private Double galonesCombustible;
    private Double costoCombustible;
    private Double costoHieloInsumos;
}
