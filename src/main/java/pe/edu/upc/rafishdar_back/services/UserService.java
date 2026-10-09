package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.UserActualizarDTO;
import pe.edu.upc.rafishdar_back.dtos.UserCambioPasswordDTO;
import pe.edu.upc.rafishdar_back.dtos.UserDTO;
import pe.edu.upc.rafishdar_back.dtos.UserRegistroDTO;
import pe.edu.upc.rafishdar_back.entities.User;

import java.util.List;

public interface UserService {
    List<UserDTO> listarTodo();

    User buscarPorId(Long id);

    UserDTO buscarPorIdDTO(Long id);

    List<UserDTO> buscarPorNombreOApellido(String termino);

    UserDTO registrar(UserRegistroDTO userRegistroDTO);

    UserDTO actualizar(UserActualizarDTO userActualizarDTO);

    UserDTO eliminarLogico(Long id);

    UserDTO activar(Long id);

    boolean cambiarPassword(UserCambioPasswordDTO userCambioPasswordDTO);
}
