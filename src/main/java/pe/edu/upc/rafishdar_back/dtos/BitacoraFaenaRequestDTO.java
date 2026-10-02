package pe.edu.upc.rafishdar_back.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BitacoraFaenaRequestDTO {
    private Long idUsuario;
    private Long idEmbarcacion;
    private Long idZona;
    private LocalDateTime fechaHoraSalida;
}
