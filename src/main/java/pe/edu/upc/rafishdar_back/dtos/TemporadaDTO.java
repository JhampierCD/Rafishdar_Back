package pe.edu.upc.rafishdar_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemporadaDTO {

    private Long id;
    private String nombreTemporada;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

}
