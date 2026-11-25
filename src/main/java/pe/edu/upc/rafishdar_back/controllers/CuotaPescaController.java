package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.dtos.cuota_pesca.CuotaPescaDTO;
import pe.edu.upc.rafishdar_back.dtos.cuota_pesca.CuotaPescaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.cuota_pesca.CuotaPescaUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.CuotaPesca;
import pe.edu.upc.rafishdar_back.entities.CuotaPesca;
import pe.edu.upc.rafishdar_back.services.CuotaPescaService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class CuotaPescaController {

    @Autowired
    CuotaPescaService cuotaPescaService;

    // http://localhost:8080/rafishdar/cuotas-pesca
    @GetMapping("/cuotas-pesca")
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

    // http://localhost:8080/rafishdar/cuotas-pesca/1
    @GetMapping("/cuotas-pesca/{id}")
    public ResponseEntity<CuotaPesca> buscarPorId(
            @PathVariable("id") Long id) {

        CuotaPesca foundCuota =
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

    // http://localhost:8080/rafishdar/cuotas-pesca
    @PostMapping("/cuotas-pesca")
    public ResponseEntity<CuotaPesca> insertar(
            @RequestBody CuotaPescaRequestDTO cuotaPescaDTO) {

        CuotaPesca newCuota = cuotaPescaService.insertarCuota(cuotaPescaDTO);

        return new ResponseEntity<>(
                newCuota,
                HttpStatus.CREATED
        );
    }

    // http://localhost:8080/rafishdar/cuotas-pesca/1
    @PutMapping("/cuotas-pesca/{id}")
    public ResponseEntity<CuotaPesca> actualizar(
            @PathVariable Long id,
            @RequestBody CuotaPescaUpdateDTO request) {

        CuotaPesca updatedCuota = cuotaPescaService.actualizarCuota(id, request);

        return new ResponseEntity<>(
                updatedCuota,
                HttpStatus.OK
        );
    }

    // http://localhost:8080/rafishdar/cuotas-pesca/1
    @DeleteMapping("/cuotas-pesca/{id}")
    public ResponseEntity<Boolean> eliminar(
            @PathVariable("id") Long id) {

        Boolean eliminado = cuotaPescaService.eliminarCuota(id);
        return new ResponseEntity<>(eliminado, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/cuotas-pesca/especie/1/temporada/2
    @GetMapping("/cuotas-pesca/especie/{especieId}/temporada/{temporadaId}")
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

    // http://localhost:8080/rafishdar/cuotas-pesca/especie/1
    @GetMapping("/cuotas-pesca/especie/{especieId}")
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

    // http://localhost:8080/rafishdar/cuotas-pesca/temporada/1
    @GetMapping("/cuotas-pesca/temporada/{temporadaId}")
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

    // http://localhost:8080/rafishdar/cuotas-pesca/temporada/1/especies
    @GetMapping("/cuotas-pesca/temporada/{temporadaId}/especies")
    public ResponseEntity<List<CuotaPescaDTO>> listarPorTemporadaConEspecie(
            @PathVariable("temporadaId") Long temporadaId) {
        List<CuotaPescaDTO> cuotas =
                cuotaPescaService.listarPorTemporadaConEspecie(temporadaId);
        if (cuotas.isEmpty()) {
            return new ResponseEntity<>(cuotas, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(cuotas, HttpStatus.OK);
    }
}