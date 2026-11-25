package pe.edu.upc.rafishdar_back.dtos.especie_pez;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspeciePezUpdateDTO {
    private String nombreComun;
    private String nombreCientifico;
    private Boolean estadoVeda;
}