package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.dtos.CuotaPescaDTO;
import pe.edu.upc.rafishdar_back.services.CuotaPescaService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")

public class CuotaPescaController {

    @Autowired
    CuotaPescaService cuotaPescaService;


    // http://localhost:8080/rafishdar/cuotas
    @GetMapping("/cuotas")
    public ResponseEntity<List<CuotaPescaDTO>> listar() {

        List<CuotaPescaDTO> foundCuotas =
                cuotaPescaService.listarTodo();

        if (foundCuotas.isEmpty()) {
            return new ResponseEntity<>(
                    foundCuotas,
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<>(
                foundCuotas,
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/cuotas/1
    @GetMapping("/cuotas/{id}")
    public ResponseEntity<CuotaPescaDTO> buscarPorId(
            @PathVariable("id") Long id) {

        CuotaPescaDTO foundCuota =
                cuotaPescaService.buscarPorId(id);

        if (foundCuota == null) {
            return new ResponseEntity<>(
                    foundCuota,
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                foundCuota,
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/cuotas
    @PostMapping("/cuotas")
    public ResponseEntity<CuotaPescaDTO> insertar(
            @RequestBody CuotaPescaDTO cuotaPescaDTO) {

        CuotaPescaDTO newCuota =
                cuotaPescaService.insertar(cuotaPescaDTO);

        if (newCuota == null) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                newCuota,
                HttpStatus.CREATED
        );
    }


    // http://localhost:8080/rafishdar/cuotas
    @PutMapping("/cuotas")
    public ResponseEntity<CuotaPescaDTO> actualizar(
            @RequestBody CuotaPescaDTO cuotaPescaDTO) {

        CuotaPescaDTO updatedCuota =
                cuotaPescaService.actualizar(cuotaPescaDTO);

        if (updatedCuota == null) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                updatedCuota,
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/cuotas/1
    @DeleteMapping("/cuotas/{id}")
    public ResponseEntity<HttpStatus> eliminar(
            @PathVariable("id") Long id) {

        if (!cuotaPescaService.eliminar(id)) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/cuotas/especie/1/temporada/2
    @GetMapping("/cuotas/especie/{especieId}/temporada/{temporadaId}")
    public ResponseEntity<CuotaPescaDTO> buscarPorEspecieYTemporada(
            @PathVariable("especieId") Long especieId,
            @PathVariable("temporadaId") Long temporadaId) {

        CuotaPescaDTO foundCuota =
                cuotaPescaService.buscarPorEspecieYTemporada(
                        especieId,
                        temporadaId
                );

        if (foundCuota == null) {
            return new ResponseEntity<>(
                    foundCuota,
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                foundCuota,
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/cuotas/especie/1
    @GetMapping("/cuotas/especie/{especieId}")
    public ResponseEntity<List<CuotaPescaDTO>> listarPorEspecie(
            @PathVariable("especieId") Long especieId) {

        List<CuotaPescaDTO> foundCuotas =
                cuotaPescaService.listarPorEspecie(especieId);

        if (foundCuotas.isEmpty()) {
            return new ResponseEntity<>(
                    foundCuotas,
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<>(
                foundCuotas,
                HttpStatus.OK
        );
    }


    // http://localhost:8080/rafishdar/cuotas/temporada/1
    @GetMapping("/cuotas/temporada/{temporadaId}")
    public ResponseEntity<List<CuotaPescaDTO>> listarPorTemporada(
            @PathVariable("temporadaId") Long temporadaId) {

        List<CuotaPescaDTO> foundCuotas =
                cuotaPescaService.listarPorTemporada(
                        temporadaId
                );

        if (foundCuotas.isEmpty()) {
            return new ResponseEntity<>(
                    foundCuotas,
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<>(
                foundCuotas,
                HttpStatus.OK
        );
    }

}
