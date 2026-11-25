package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.entities.ZonaPesca;
import pe.edu.upc.rafishdar_back.dtos.zona_pesca.ZonaPescaDTO;
import pe.edu.upc.rafishdar_back.dtos.zona_pesca.ZonaPescaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.zona_pesca.ZonaPescaUpdateDTO;
import pe.edu.upc.rafishdar_back.services.ZonaPescaService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class ZonaPescaController {

    @Autowired
    ZonaPescaService zonaPescaService;

    // http://localhost:8080/rafishdar/zonas-pesca
    @GetMapping("/zonas-pesca")
    public ResponseEntity<List<ZonaPescaDTO>> listarZonaPesca() {

        List<ZonaPescaDTO> foundZonasPesca =
                zonaPescaService.listarZonaPescaDTO();

        if (foundZonasPesca.isEmpty()) {
            return new ResponseEntity<>(
                    foundZonasPesca,
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<>(
                foundZonasPesca,
                HttpStatus.OK
        );
    }

    // http://localhost:8080/rafishdar/zonas-pesca/top3/especie/1?mes=5
    @GetMapping("/zonas-pesca/top3/especie/{especieId}")
    public ResponseEntity<List<ZonaPesca>> listarTop3Historicas(
            @PathVariable Long especieId,
            @RequestParam int mes) {
        if (mes < 1 || mes > 12) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<ZonaPesca> zonas =
                zonaPescaService.listarTop3Historicas(especieId, mes);
        if (zonas.isEmpty()) {
            return new ResponseEntity<>(zonas, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(zonas, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/zonas-pesca/cercanas?distanciaMaxima=10.0
    @GetMapping("/zonas-pesca/cercanas")
    public ResponseEntity<List<ZonaPesca>> listarCercanasACosta(
            @RequestParam Double distanciaMaxima) {
        if (distanciaMaxima < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<ZonaPesca> zonas =
                zonaPescaService.listarCercanasACosta(distanciaMaxima);
        if (zonas.isEmpty()) {
            return new ResponseEntity<>(zonas, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(zonas, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/zonas-pesca/1
    @GetMapping("/zonas-pesca/{id}")
    public ResponseEntity<ZonaPesca> buscarporId(
            @PathVariable("id") Long id) {

        ZonaPesca foundZonaPesca =
                zonaPescaService.buscarPorId(id);

        if (foundZonaPesca == null) {
            return new ResponseEntity<>(
                    foundZonaPesca,
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                foundZonaPesca,
                HttpStatus.OK
        );
    }

    // http://localhost:8080/rafishdar/zonas-pesca
    @PostMapping("/zonas-pesca")
    public ResponseEntity<ZonaPesca> insertar(
            @RequestBody ZonaPescaRequestDTO request) {

        ZonaPesca newZonaPesca =
                zonaPescaService.insertar(request);

        return new ResponseEntity<>(
                newZonaPesca,
                HttpStatus.CREATED
        );
    }

    // http://localhost:8080/rafishdar/zonas-pesca/1
    @DeleteMapping("/zonas-pesca/{id}")
    public ResponseEntity<Boolean> eliminar(
            @PathVariable("id") Long id) {

        Boolean eliminado = zonaPescaService.eliminar(id);
        return new ResponseEntity<>(eliminado, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/zonas-pesca
    @PutMapping("/zonas-pesca/{id}")
    public ResponseEntity<ZonaPesca> actualizar(
            @PathVariable("id") Long id,
            @RequestBody ZonaPescaUpdateDTO request) {
        ZonaPesca zonaPesca = zonaPescaService.actualizar(id, request);
        return new ResponseEntity<>(
                zonaPesca,
                HttpStatus.OK
        );
    }
}