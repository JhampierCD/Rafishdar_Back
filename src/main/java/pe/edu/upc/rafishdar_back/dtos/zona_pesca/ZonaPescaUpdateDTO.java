package pe.edu.upc.rafishdar_back.dtos.zona_pesca;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZonaPescaUpdateDTO {
    private String nombreZona;
    private Double latitud;
    private Double longitud;
    private Double distanciaCostaKm;
    private Double radioAreaKm;
}