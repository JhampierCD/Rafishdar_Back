package pe.edu.upc.rafishdar_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class EspeciePezDTO {

    private Long id;
    private String nombreComun;
    private String nombreCientifico;
    private Boolean estadoVeda;

}
