package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.dtos.TemporadaDTO;
import pe.edu.upc.rafishdar_back.services.TemporadaService;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class TemporadaController {

    @Autowired
    TemporadaService temporadaService;


    // http://localhost:8080/rafishdar/temporadas
    @GetMapping("/temporadas")
    public ResponseEntity<List<TemporadaDTO>> listar() {

        List<TemporadaDTO> foundTemporadas =
                temporadaService.listarTodo();

        if (foundTemporadas.isEmpty()) {
            return new ResponseEntity<>(
                    foundTemporadas,
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<>(
                foundTemporadas,
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/temporadas/1
    @GetMapping("/temporadas/{id}")
    public ResponseEntity<TemporadaDTO> buscarPorId(
            @PathVariable("id") Long id) {

        TemporadaDTO foundTemporada =
                temporadaService.buscarPorId(id);

        if (foundTemporada == null) {
            return new ResponseEntity<>(
                    foundTemporada,
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                foundTemporada,
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/temporadas
    @PostMapping("/temporadas")
    public ResponseEntity<TemporadaDTO> insertar(
            @RequestBody TemporadaDTO temporadaDTO) {

        TemporadaDTO newTemporada =
                temporadaService.insertar(temporadaDTO);

        if (newTemporada == null) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                newTemporada,
                HttpStatus.CREATED
        );
    }


    // http://localhost:8080/rafishdar/temporadas
    @PutMapping("/temporadas")
    public ResponseEntity<TemporadaDTO> actualizar(
            @RequestBody TemporadaDTO temporadaDTO) {

        TemporadaDTO updatedTemporada =
                temporadaService.actualizar(temporadaDTO);

        if (updatedTemporada == null) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                updatedTemporada,
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/temporadas/1
    @DeleteMapping("/temporadas/{id}")
    public ResponseEntity<HttpStatus> eliminar(
            @PathVariable("id") Long id) {

        if (!temporadaService.eliminar(id)) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/temporadas/vigentes
    @GetMapping("/temporadas/vigentes")
    public ResponseEntity<List<TemporadaDTO>> listarVigentes() {

        List<TemporadaDTO> foundTemporadas =
                temporadaService.listarVigentes();

        if (foundTemporadas.isEmpty()) {
            return new ResponseEntity<>(
                    foundTemporadas,
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<>(
                foundTemporadas,
                HttpStatus.OK
        );
    }

    @GetMapping("/temporadas/superpuestas")
    public ResponseEntity<List<TemporadaDTO>> buscarSuperpuestas(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin) {
        if (inicio.isAfter(fin)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<TemporadaDTO> temporadas =
                temporadaService.buscarSuperpuestas(inicio, fin);
        if (temporadas.isEmpty()) {
            return new ResponseEntity<>(temporadas, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(temporadas, HttpStatus.OK);
    }

}
