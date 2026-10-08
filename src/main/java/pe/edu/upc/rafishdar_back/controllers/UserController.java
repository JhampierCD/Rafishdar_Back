package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.dtos.TokenDTO;
import pe.edu.upc.rafishdar_back.dtos.UserActualizarDTO;
import pe.edu.upc.rafishdar_back.dtos.UserCambioPasswordDTO;
import pe.edu.upc.rafishdar_back.dtos.UserDTO;
import pe.edu.upc.rafishdar_back.dtos.UserRegistroDTO;
import pe.edu.upc.rafishdar_back.entities.User;
import pe.edu.upc.rafishdar_back.security.JwtUtilService;
import pe.edu.upc.rafishdar_back.security.UserSecurity;
import pe.edu.upc.rafishdar_back.serviceimpl.UserDetailsServiceImpl;
import pe.edu.upc.rafishdar_back.services.UserService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    JwtUtilService jwtUtilService;

    // http://localhost:8080/rafishdar/users
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> listar() {

        List<UserDTO> foundUsers =
                userService.listarTodo();

        if (foundUsers.isEmpty()) {
            return new ResponseEntity<>(
                    foundUsers,
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<>(
                foundUsers,
                HttpStatus.OK
        );
    }

    // http://localhost:8080/rafishdar/users/1
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> buscarPorId(
            @PathVariable("id") Long id) {

        UserDTO foundUser =
                userService.buscarPorId(id);

        if (foundUser == null) {
            return new ResponseEntity<>(
                    foundUser,
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                foundUser,
                HttpStatus.OK
        );
    }

    // http://localhost:8080/rafishdar/users/register
    @PostMapping("/users/register")
    public ResponseEntity<UserDTO> registrar(
            @RequestBody UserRegistroDTO userRegistroDTO) {

        UserDTO newUser =
                userService.registrar(
                        userRegistroDTO
                );

        if (newUser == null) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                newUser,
                HttpStatus.CREATED
        );
    }

    // http://localhost:8080/rafishdar/users/login
    @PostMapping("/users/login")
    public ResponseEntity<TokenDTO> login(
            @RequestBody User user) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getCorreo(),
                            user.getPassword()
                    )
            );

            UserSecurity securityUser =
                    (UserSecurity)
                            userDetailsServiceImpl
                                    .loadUserByUsername(
                                            user.getCorreo()
                                    );

            String token =
                    jwtUtilService.generateToken(
                            securityUser
                    );

            String authorities =
                    securityUser
                            .getAuthorities()
                            .stream()
                            .map(
                                    n -> n.getAuthority()
                            )
                            .toList()
                            .toString();

            TokenDTO tokenDTO =
                    new TokenDTO(
                            token,
                            securityUser
                                    .getUser()
                                    .getId(),
                            authorities
                    );

            return new ResponseEntity<>(
                    tokenDTO,
                    HttpStatus.OK
            );

        } catch (Exception e) {

            return new ResponseEntity<>(
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    // http://localhost:8080/rafishdar/users
    @PutMapping("/users")
    public ResponseEntity<UserDTO> actualizar(
            @RequestBody UserActualizarDTO userActualizarDTO) {

        UserDTO updatedUser =
                userService.actualizar(
                        userActualizarDTO
                );

        if (updatedUser == null) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                updatedUser,
                HttpStatus.OK
        );
    }

    // http://localhost:8080/rafishdar/users/1/estado/Inactivo
    @PutMapping("/users/{id}/estado/{estado}")
    public ResponseEntity<UserDTO> cambiarEstado(
            @PathVariable("id") Long id,
            @PathVariable("estado") String estado) {

        UserDTO updatedUser =
                userService.cambiarEstado(
                        id,
                        estado
                );

        if (updatedUser == null) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                updatedUser,
                HttpStatus.OK
        );
    }

    // http://localhost:8080/rafishdar/users/password
    @PutMapping("/users/password")
    public ResponseEntity<HttpStatus> cambiarPassword(
            @RequestBody UserCambioPasswordDTO userCambioPasswordDTO) {

        if (!userService.cambiarPassword(
                userCambioPasswordDTO
        )) {

            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                HttpStatus.OK
        );
    }
}