package pe.edu.upc.rafishdar_back.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.dtos.UserActualizarDTO;
import pe.edu.upc.rafishdar_back.dtos.UserCambioPasswordDTO;
import pe.edu.upc.rafishdar_back.dtos.UserDTO;
import pe.edu.upc.rafishdar_back.dtos.UserRegistroDTO;
import pe.edu.upc.rafishdar_back.services.UserService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class UserController {
    @Autowired
    UserService userService;


    // http://localhost:8080/rafishdar/usuarios
    @GetMapping("/usuarios")
    public ResponseEntity<List<UserDTO>> listar() {

        List<UserDTO> foundUsers = userService.listarTodo();

        if (foundUsers.isEmpty()) {
            return new ResponseEntity<>(foundUsers, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(foundUsers, HttpStatus.OK);
    }


    // http://localhost:8080/rafishdar/usuarios/1
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UserDTO> buscarPorId(@PathVariable("id") Long id) {

        UserDTO foundUser = userService.buscarPorId(id);

        if (foundUser == null) {
            return new ResponseEntity<>(
                    foundUser,
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(foundUser, HttpStatus.OK);
    }


    // http://localhost:8080/rafishdar/usuarios
    @PostMapping("/usuarios")
    public ResponseEntity<UserDTO> registrar(@RequestBody UserRegistroDTO userRegistroDTO) {

        UserDTO newUser = userService.registrar(userRegistroDTO);

        if (newUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }


    // http://localhost:8080/rafishdar/usuarios
    @PutMapping("/usuarios")
    public ResponseEntity<UserDTO> actualizar(@RequestBody UserActualizarDTO userActualizarDTO) {

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


    // http://localhost:8080/rafishdar/usuarios/1/estado/Inactivo
    @PutMapping("/usuarios/{id}/estado/{estado}")
    public ResponseEntity<UserDTO> cambiarEstado(@PathVariable("id") Long id, @PathVariable("estado") String estado) {

        UserDTO updatedUser = userService.cambiarEstado(id, estado);

        if (updatedUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }


    // http://localhost:8080/rafishdar/usuarios/password
    @PutMapping("/usuarios/password")
    public ResponseEntity<HttpStatus> cambiarPassword(@RequestBody UserCambioPasswordDTO userCambioPasswordDTO) {

        if (!userService.cambiarPassword(userCambioPasswordDTO)) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
