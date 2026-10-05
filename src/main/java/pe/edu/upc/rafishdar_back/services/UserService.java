package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.UserActualizarDTO;
import pe.edu.upc.rafishdar_back.dtos.UserCambioPasswordDTO;
import pe.edu.upc.rafishdar_back.dtos.UserDTO;
import pe.edu.upc.rafishdar_back.dtos.UserRegistroDTO;
import pe.edu.upc.rafishdar_back.entities.User;

import java.util.List;

public interface UserService {
    List<UserDTO> listarTodo();

    UserDTO buscarPorIdDTO(Long id);

    User buscarPorId(Long id);

    UserDTO registrar(UserRegistroDTO userRegistroDTO);

    UserDTO actualizar(UserActualizarDTO userActualizarDTO);

    UserDTO cambiarEstado(Long id, String estado);

    boolean cambiarPassword(UserCambioPasswordDTO userCambioPasswordDTO);
}
