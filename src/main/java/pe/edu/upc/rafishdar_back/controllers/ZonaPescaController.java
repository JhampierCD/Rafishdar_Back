package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.entities.ZonaPesca;
import pe.edu.upc.rafishdar_back.services.ZonaPescaService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class ZonaPescaController {

    @Autowired
    ZonaPescaService zonaPescaService;

    @GetMapping("/zonas-pesca")
    public ResponseEntity<List<ZonaPesca>> listarZonaPesca() {

        List<ZonaPesca> foundZonasPesca =
                zonaPescaService.listarZonaPesca();

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

    @PostMapping("/zonas-pesca")
    public ResponseEntity<ZonaPesca> insertar(
            @RequestBody ZonaPesca zonaPesca) {

        ZonaPesca newZonaPesca =
                zonaPescaService.insertar(zonaPesca);

        if (newZonaPesca == null) {
            return new ResponseEntity<>(
                    newZonaPesca,
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                newZonaPesca,
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/zonas-pesca/{id}")
    public ResponseEntity<HttpStatus> eliminar(
            @PathVariable("id") Long id) {

        if (!zonaPescaService.eliminar(id)) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                HttpStatus.OK
        );
    }

    @PutMapping("/zonas-pesca")
    public ResponseEntity<ZonaPesca> actualizar(
            @RequestBody ZonaPesca zonaPesca) {

        if (zonaPescaService.actualizar(zonaPesca) == null) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                zonaPesca,
                HttpStatus.OK
        );
    }
}