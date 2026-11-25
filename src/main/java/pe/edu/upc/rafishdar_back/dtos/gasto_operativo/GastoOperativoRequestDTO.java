package pe.edu.upc.rafishdar_back.dtos.gasto_operativo;

import lombok.Data;

@Data
public class GastoOperativoRequestDTO {
    private Double galonesCombustible;
    private Double costoCombustible;
    private Double costoHieloInsumos;
}