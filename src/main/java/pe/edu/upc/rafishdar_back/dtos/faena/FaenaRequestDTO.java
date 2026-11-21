package pe.edu.upc.rafishdar_back.dtos.faena;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FaenaRequestDTO {
    private LocalDateTime fechaHoraSalidaProgramada;
    private LocalDateTime fechaHoraLlegadaEstimada;
    private Long embarcacionId;
}
