package pe.edu.upc.rafishdar_back.dtos.detalle_captura;

import lombok.Data;

@Data
public class DetalleCapturaRequestDTO {
    private Double volumenKg;
    private Long bitacoraId;
    private Long especieId;
}
