package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.user.UserActualizarDTO;
import pe.edu.upc.rafishdar_back.dtos.user.UserCambioPasswordDTO;
import pe.edu.upc.rafishdar_back.dtos.user.UserDTO;
import pe.edu.upc.rafishdar_back.dtos.user.UserRegistroDTO;
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
