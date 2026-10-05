package pe.edu.upc.rafishdar_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCambioPasswordDTO {
    private Long userId;
    private String passwordActual;
    private String passwordNueva;
}
