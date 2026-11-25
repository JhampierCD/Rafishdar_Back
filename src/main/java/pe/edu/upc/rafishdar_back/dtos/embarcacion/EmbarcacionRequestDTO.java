package pe.edu.upc.rafishdar_back.dtos.embarcacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmbarcacionRequestDTO {
    private String nombre;
    private String matricula;
    private Double capacidadToneladas;
    private Long usuarioId;
}