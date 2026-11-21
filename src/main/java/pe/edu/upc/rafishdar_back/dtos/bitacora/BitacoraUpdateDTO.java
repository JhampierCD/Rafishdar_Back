package pe.edu.upc.rafishdar_back.dtos.bitacora;

import lombok.Data;

@Data
public class BitacoraUpdateDTO {
    // Los campos nulos conservan su valor actual; las coordenadas se actualizan en pareja.
    private Double latitud;
    private Double longitud;
    private Long zonaPescaId;
    private String observaciones;
}
