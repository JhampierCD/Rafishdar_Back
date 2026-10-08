package pe.edu.upc.rafishdar_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecomendacionIADTO {
    private Integer idRecomendacion;
    private Integer idBitacora;
    private String analisisTexto;
    private String nivelRiesgo;
    private LocalDateTime fechaGeneracion;
}
