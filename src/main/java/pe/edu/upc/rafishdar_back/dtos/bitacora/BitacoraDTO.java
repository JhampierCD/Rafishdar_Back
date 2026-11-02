package pe.edu.upc.rafishdar_back.dtos.bitacora;

import lombok.Data;
import pe.edu.upc.rafishdar_back.dtos.CondicionClimaticaDTO;
import pe.edu.upc.rafishdar_back.dtos.detalle_captura.DetalleCapturaDTO;

import java.time.LocalDateTime;
import java.util.List;

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
