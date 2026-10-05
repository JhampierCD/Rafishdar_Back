package pe.edu.upc.rafishdar_back.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.dtos.EspeciePezDTO;
import pe.edu.upc.rafishdar_back.services.EspeciePezService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class EspeciePezController {

    @Autowired
    EspeciePezService especiePezService;


    // http://localhost:8080/rafishdar/especies
    @GetMapping("/especies")
    public ResponseEntity<List<EspeciePezDTO>> listar() {

        List<EspeciePezDTO> foundEspecies =
                especiePezService.listarTodo();

        if (foundEspecies.isEmpty()) {
            return new ResponseEntity<>(
                    foundEspecies,
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<>(
                foundEspecies,
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/especies/1
    @GetMapping("/especies/{id}")
    public ResponseEntity<EspeciePezDTO> buscarPorId(
            @PathVariable("id") Long id) {

        EspeciePezDTO foundEspecie =
                especiePezService.buscarPorId(id);

        if (foundEspecie == null) {
            return new ResponseEntity<>(
                    foundEspecie,
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                foundEspecie,
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/especies
    @PostMapping("/especies")
    public ResponseEntity<EspeciePezDTO> insertar(
            @RequestBody EspeciePezDTO especiePezDTO) {

        EspeciePezDTO newEspecie =
                especiePezService.insertar(especiePezDTO);

        if (newEspecie == null) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                newEspecie,
                HttpStatus.CREATED
        );
    }


    // http://localhost:8080/rafishdar/especies
    @PutMapping("/especies")
    public ResponseEntity<EspeciePezDTO> actualizar(
            @RequestBody EspeciePezDTO especiePezDTO) {

        EspeciePezDTO updatedEspecie =
                especiePezService.actualizar(especiePezDTO);

        if (updatedEspecie == null) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                updatedEspecie,
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/especies/1
    @DeleteMapping("/especies/{id}")
    public ResponseEntity<HttpStatus> eliminar(
            @PathVariable("id") Long id) {

        if (!especiePezService.eliminar(id)) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/especies/veda/true
    @GetMapping("/especies/veda/{estado}")
    public ResponseEntity<List<EspeciePezDTO>> listarPorEstadoVeda(
            @PathVariable("estado") Boolean estado) {

        List<EspeciePezDTO> foundEspecies =
                especiePezService.listarPorEstadoVeda(estado);

        if (foundEspecies.isEmpty()) {
            return new ResponseEntity<>(
                    foundEspecies,
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<>(
                foundEspecies,
                HttpStatus.OK
        );
    }

}
