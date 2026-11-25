package pe.edu.upc.rafishdar_back.dtos.bitacora;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BitacoraDTO {
    private Long id;
    private Double latitud;
    private Double longitud;
    private LocalDateTime fechaHoraRegistro;
    private String observaciones;

    // Llaves foráneas para facilitar referencias rápidas
    private Long faenaId;
    private Long zonaPescaId;
}