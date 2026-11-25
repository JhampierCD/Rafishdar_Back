package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.dtos.temporada.TemporadaDTO;
import pe.edu.upc.rafishdar_back.dtos.temporada.TemporadaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.temporada.TemporadaUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.Temporada;
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
    public ResponseEntity<Temporada> buscarPorId(
            @PathVariable("id") Long id) {

        Temporada foundTemporada =
                temporadaService.buscarPorId(id);
        return new ResponseEntity<>(
                foundTemporada,
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/temporadas
    @PostMapping("/temporadas")
    public ResponseEntity<Temporada> insertar(
            @RequestBody TemporadaRequestDTO request) {

        Temporada newTemporada =
                temporadaService.insertar(request);

        return new ResponseEntity<>(
                newTemporada,
                HttpStatus.CREATED
        );
    }


    // http://localhost:8080/rafishdar/temporadas
    @PutMapping("/temporadas/{id}")
    public ResponseEntity<Temporada> actualizar(
            @PathVariable("id") Long id,
            @RequestBody TemporadaUpdateDTO request) {

        Temporada updatedTemporada =
                temporadaService.actualizar(id, request);

        return new ResponseEntity<>(
                updatedTemporada,
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/temporadas/1
    @DeleteMapping("/temporadas/{id}")
    public ResponseEntity<Boolean> eliminar(
            @PathVariable("id") Long id) {

        Boolean eliminado = temporadaService.eliminar(id);
        return new ResponseEntity<>(eliminado, HttpStatus.OK);
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

    // http://localhost:8080/rafishdar/temporadas/superpuestas?inicio=2024-01-01&fin=2024-12-31
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