package pe.edu.upc.rafishdar_back.dtos.faena;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaenaDTO {
    private Long id;
    private LocalDateTime fechaHoraSalida;
    private LocalDateTime fechaHoraLlegada;
    private String estado;
    private Long usuarioId;
    private Long embarcacionId;
}
