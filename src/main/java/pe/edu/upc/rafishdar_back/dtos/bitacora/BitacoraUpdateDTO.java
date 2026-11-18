package pe.edu.upc.rafishdar_back.dtos.bitacora;

import lombok.Data;

@Data
public class BitacoraUpdateDTO {
    private Long id;
    private Double latitud;
    private Double longitud;
    private Long zonaPescaId;
    private String observaciones;
}
