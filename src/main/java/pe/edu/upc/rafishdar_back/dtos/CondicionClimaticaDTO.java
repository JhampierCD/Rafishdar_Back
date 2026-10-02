package pe.edu.upc.rafishdar_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CondicionClimaticaDTO {
    private Integer idCondicion;
    private Integer idBitacora;
    private Double temperaturaCelsius;
    private Double velocidadVientoNudos;
    private String estadoOleaje;
}
