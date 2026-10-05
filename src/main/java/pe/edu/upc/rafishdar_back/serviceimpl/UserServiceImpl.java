package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.UserActualizarDTO;
import pe.edu.upc.rafishdar_back.dtos.UserCambioPasswordDTO;
import pe.edu.upc.rafishdar_back.dtos.UserDTO;
import pe.edu.upc.rafishdar_back.dtos.UserRegistroDTO;
import pe.edu.upc.rafishdar_back.entities.User;
import pe.edu.upc.rafishdar_back.repositories.UserRepository;
import pe.edu.upc.rafishdar_back.services.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public List<UserDTO> listarTodo() {

        List<User> users = userRepository.findAll();

        return convertirLista(users);
    }


    @Override
    public UserDTO buscarPorId(Long id) {

        if (id == null) {
            return null;
        }

        User user = userRepository
                .findById(id)
                .orElse(null);

        if (user == null) {
            return null;
        }

        return convertirDTO(user);
    }


    @Override
    public UserDTO registrar(UserRegistroDTO userRegistroDTO) {

        if (!datosRegistroValidos(userRegistroDTO)) {
            return null;
        }

        String correo = userRegistroDTO
                .getCorreo()
                .trim()
                .toLowerCase();

        if (userRepository.existsByCorreoIgnoreCase(correo)) {
            return null;
        }

        User user = new User();

        user.setNombres(
                userRegistroDTO
                        .getNombres()
                        .trim()
        );

        user.setApellidos(
                userRegistroDTO
                        .getApellidos()
                        .trim()
        );

        user.setCorreo(correo);

        user.setPassword(userRegistroDTO.getPassword()
        );

        user.setEstado("Activo");

        User nuevoUser = userRepository.save(user);

        return convertirDTO(nuevoUser);
    }


    @Override
    public UserDTO actualizar(UserActualizarDTO userActualizarDTO) {

        if (!datosActualizacionValidos(userActualizarDTO)) {
            return null;
        }

        User user = userRepository
                .findById(userActualizarDTO.getId())
                .orElse(null);

        if (user == null) {
            return null;
        }

        String correo = userActualizarDTO
                .getCorreo()
                .trim()
                .toLowerCase();

        User userConMismoCorreo = userRepository.findByCorreoIgnoreCase(correo);

        if (userConMismoCorreo != null && !userConMismoCorreo.getId().equals(user.getId())) {
            return null;
        }

        user.setNombres(userActualizarDTO.getNombres().trim());
        user.setApellidos(userActualizarDTO.getApellidos().trim());
        user.setCorreo(correo);
        User userActualizado = userRepository.save(user);
        return convertirDTO(userActualizado);
    }


    @Override
    public UserDTO cambiarEstado(Long id, String estado) {

        if (id == null || estado == null || estado.trim().isEmpty()) {
            return null;
        }

        String nuevoEstado = normalizarEstado(estado);

        if (nuevoEstado == null) {
            return null;
        }

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return null;
        }

        user.setEstado(nuevoEstado);

        User userActualizado = userRepository.save(user);

        return convertirDTO(userActualizado);
    }


    @Override
    public boolean cambiarPassword(UserCambioPasswordDTO userCambioPasswordDTO) {

        if (!datosCambioPasswordValidos(
                userCambioPasswordDTO)) {
            return false;
        }

        User user = userRepository.findById(userCambioPasswordDTO.getUserId()).orElse(null);

        if (user == null) {
            return false;
        }

        if (!user.getPassword().equals(userCambioPasswordDTO.getPasswordActual())) {
            return false;
        }

        if (user.getPassword().equals(userCambioPasswordDTO.getPasswordNueva())) {
            return false;
        }

        user.setPassword(userCambioPasswordDTO.getPasswordNueva());

        userRepository.save(user);

        return true;
    }


    private boolean datosRegistroValidos(UserRegistroDTO dto) {

        if (dto == null) {
            return false;
        }

        if (!datosPersonalesValidos(
                dto.getNombres(),
                dto.getApellidos(),
                dto.getCorreo())) {

            return false;
        }

        if (!passwordValida(
                dto.getPassword())) {

            return false;
        }

        return true;
    }


    private boolean datosActualizacionValidos(UserActualizarDTO dto) {

        if (dto == null ||
                dto.getId() == null) {

            return false;
        }

        return datosPersonalesValidos(dto.getNombres(), dto.getApellidos(), dto.getCorreo());
    }


    private boolean datosCambioPasswordValidos(UserCambioPasswordDTO dto) {

        if (dto == null) {
            return false;
        }

        if (dto.getUserId() == null) {
            return false;
        }

        if (dto.getPasswordActual() == null ||
                dto.getPasswordActual()
                        .trim()
                        .isEmpty()) {

            return false;
        }

        if (!passwordValida(
                dto.getPasswordNueva())) {

            return false;
        }

        return true;
    }


    private boolean datosPersonalesValidos(String nombres, String apellidos, String correo) {

        if (nombres == null || nombres.trim().isEmpty()) {
            return false;
        }

        if (apellidos == null || apellidos.trim().isEmpty()) {
            return false;
        }

        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }

        if (nombres.trim().length() > 100) {
            return false;
        }

        if (apellidos.trim().length() > 100) {
            return false;
        }

        if (correo.trim().length() > 150) {
            return false;
        }

        if (!correo.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return false;
        }

        return true;
    }


    private boolean passwordValida(String password) {

        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        if (password.length() < 6) {
            return false;
        }

        if (password.length() > 100) {
            return false;
        }

        return true;
    }


    private String normalizarEstado(String estado) {

        if (estado.equalsIgnoreCase("Activo")) {
            return "Activo";
        }

        if (estado.equalsIgnoreCase("Inactivo")) {
            return "Inactivo";
        }

        return null;
    }


    private UserDTO convertirDTO(User user) {

        UserDTO dto = new UserDTO();

        dto.setId(user.getId());
        dto.setNombres(user.getNombres());
        dto.setApellidos(user.getApellidos());
        dto.setCorreo(user.getCorreo());
        dto.setEstado(user.getEstado());

        return dto;
    }


    private List<UserDTO> convertirLista(List<User> users) {

        List<UserDTO> listaDTO =
                new ArrayList<>();

        for (User user : users) {

            listaDTO.add(
                    convertirDTO(user)
            );
        }

        return listaDTO;
    }

}
