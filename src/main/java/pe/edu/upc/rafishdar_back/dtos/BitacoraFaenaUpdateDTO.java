package pe.edu.upc.rafishdar_back.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BitacoraFaenaUpdateDTO {
    private LocalDateTime fechaHoraSalida;
    private Long idEmbarcacion;
    private Long idZona;
}
