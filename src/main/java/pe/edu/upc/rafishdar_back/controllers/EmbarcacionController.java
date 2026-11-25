package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.entities.Embarcacion;
import pe.edu.upc.rafishdar_back.dtos.embarcacion.EmbarcacionDTO;
import pe.edu.upc.rafishdar_back.dtos.embarcacion.EmbarcacionRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.embarcacion.EmbarcacionUpdateDTO;
import pe.edu.upc.rafishdar_back.services.EmbarcacionService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class EmbarcacionController {

    @Autowired
    EmbarcacionService embarcacionService;

    // http://localhost:8080/rafishdar/embarcaciones
    @GetMapping("/embarcaciones")
    public ResponseEntity<List<EmbarcacionDTO>> listarEmbarcacion() {

        List<EmbarcacionDTO> foundEmbarcaciones =
                embarcacionService.listarEmbarcacionesDTO();

        if (foundEmbarcaciones.isEmpty()) {
            return new ResponseEntity<>(
                    foundEmbarcaciones,
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<>(
                foundEmbarcaciones,
                HttpStatus.OK
        );
    }

    // http://localhost:8080/rafishdar/embarcaciones/usuario/1
    @GetMapping("/embarcaciones/usuario/{usuarioId}")
    public ResponseEntity<List<Embarcacion>> listarPorUsuario(
            @PathVariable Long usuarioId) {
        List<Embarcacion> embarcaciones =
                embarcacionService.listarPorUsuario(usuarioId);
        if (embarcaciones.isEmpty()) {
            return new ResponseEntity<>(embarcaciones, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(embarcaciones, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/embarcaciones/matricula/{matricula}/existe
    @GetMapping("/embarcaciones/matricula/{matricula}/existe")
    public ResponseEntity<Boolean> existeMatricula(
            @PathVariable String matricula) {
        if (matricula.isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(
                embarcacionService.existeMatricula(matricula),
                HttpStatus.OK
        );
    }

    // http://localhost:8080/rafishdar/embarcaciones/1
    @GetMapping("/embarcaciones/{id}")
    public ResponseEntity<Embarcacion> buscarporId(
            @PathVariable("id") Long id) {

        Embarcacion foundEmbarcacion =
                embarcacionService.buscarPorId(id);

        if (foundEmbarcacion == null) {
            return new ResponseEntity<>(
                    foundEmbarcacion,
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                foundEmbarcacion,
                HttpStatus.OK
        );
    }

    // http://localhost:8080/rafishdar/embarcaciones
    @PostMapping("/embarcaciones")
    public ResponseEntity<Embarcacion> insertar(
            @RequestBody EmbarcacionRequestDTO request) {

        Embarcacion newEmbarcacion =
                embarcacionService.insertar(request);

        return new ResponseEntity<>(
                newEmbarcacion,
                HttpStatus.CREATED
        );
    }

    // http://localhost:8080/rafishdar/embarcaciones/1
    @DeleteMapping("/embarcaciones/{id}")
    public ResponseEntity<Boolean> eliminar(
            @PathVariable("id") Long id) {

        Boolean eliminado = embarcacionService.eliminar(id);
        return new ResponseEntity<>(eliminado, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/embarcaciones
    @PutMapping("/embarcaciones/{id}")
    public ResponseEntity<Embarcacion> actualizar(
            @PathVariable("id") Long id,
            @RequestBody EmbarcacionUpdateDTO request) {
        Embarcacion embarcacion = embarcacionService.actualizar(id, request);
        return new ResponseEntity<>(
                embarcacion,
                HttpStatus.OK
        );
    }
}