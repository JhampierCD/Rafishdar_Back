package pe.edu.upc.rafishdar_back.dtos.faena;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaenaUpdateDTO {
    private LocalDateTime fechaHoraSalidaProgramada;
    private LocalDateTime fechaHoraLlegadaEstimada;
    private Long embarcacionId;
}