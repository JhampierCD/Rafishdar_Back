package pe.edu.upc.rafishdar_back.dtos.detalle_captura;

import lombok.Data;

@Data
public class DetalleCapturaDTO {
    private Long id;
    private Double volumenKg;
    private Long bitacoraId;
    private Long especieId;

}
