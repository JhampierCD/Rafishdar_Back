package pe.edu.upc.rafishdar_back.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.dtos.especie_pez.EspeciePezDTO;
import pe.edu.upc.rafishdar_back.dtos.especie_pez.EspeciePezRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.especie_pez.EspeciePezUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.EspeciePez;
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
    public ResponseEntity<EspeciePez> buscarPorId(
            @PathVariable("id") Long id) {

        EspeciePez foundEspecie =
                especiePezService.buscarPorId(id);
        return new ResponseEntity<>(
                foundEspecie,
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/especies
    @PostMapping("/especies")
    public ResponseEntity<EspeciePez> insertar(
            @RequestBody EspeciePezRequestDTO request) {

        EspeciePez newEspecie =
                especiePezService.insertar(request);

        return new ResponseEntity<>(
                newEspecie,
                HttpStatus.CREATED
        );
    }


    // http://localhost:8080/rafishdar/especies
    @PutMapping("/especies/{id}")
    public ResponseEntity<EspeciePez> actualizar(
            @PathVariable("id") Long id,
            @RequestBody EspeciePezUpdateDTO request) {

        EspeciePez updatedEspecie =
                especiePezService.actualizar(id, request);

        return new ResponseEntity<>(
                updatedEspecie,
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/especies/1
    @DeleteMapping("/especies/{id}")
    public ResponseEntity<Boolean> eliminar(
            @PathVariable("id") Long id) {

        Boolean eliminado = especiePezService.eliminar(id);
        return new ResponseEntity<>(eliminado, HttpStatus.OK);
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

    // http://localhost:8080/rafishdar/especies/buscar?nombreComun=anchoveta
    @GetMapping("/especies/buscar")
    public ResponseEntity<List<EspeciePezDTO>> buscarPorNombreComun(
            @RequestParam("nombreComun") String nombreComun) {
        if (nombreComun.isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<EspeciePezDTO> especies = especiePezService.buscarPorNombreComun(nombreComun);
        if (especies.isEmpty()) {
            return new ResponseEntity<>(especies, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(especies, HttpStatus.OK);
    }

}