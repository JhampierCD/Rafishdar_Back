package pe.edu.upc.rafishdar_back.dtos.faena;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FaenaRequestDTO {
    private LocalDateTime fechaHoraSalida;
    private LocalDateTime fechaHoraLlegada;
    private Long embarcacionId;
}
