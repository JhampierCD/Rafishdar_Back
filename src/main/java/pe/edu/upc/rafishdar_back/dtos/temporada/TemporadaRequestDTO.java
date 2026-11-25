package pe.edu.upc.rafishdar_back.dtos.temporada;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemporadaRequestDTO {
    private String nombreTemporada;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}