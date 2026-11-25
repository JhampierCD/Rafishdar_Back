package pe.edu.upc.rafishdar_back.dtos.gasto_operativo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GastoOperativoUpdateDTO {
    private Double galonesCombustible;
    private Double costoCombustible;
    private Double costoHieloInsumos;
}