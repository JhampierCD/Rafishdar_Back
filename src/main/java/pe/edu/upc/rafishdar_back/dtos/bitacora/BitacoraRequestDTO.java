package pe.edu.upc.rafishdar_back.dtos.bitacora;

import lombok.Data;

@Data
public class BitacoraRequestDTO {

    private Long faenaId;

    // Puede ser nulo; el backend hereda la zona de la bitácora anterior si es necesario
    private Long zonaPescaId;
    private Double latitud;
    private Double longitud;
    private String observaciones;
}
