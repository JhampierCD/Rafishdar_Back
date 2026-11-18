package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.faena.FaenaDTO;
import pe.edu.upc.rafishdar_back.dtos.faena.FaenaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.faena.FaenaUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.Bitacora;
import pe.edu.upc.rafishdar_back.entities.Faena;
import pe.edu.upc.rafishdar_back.services.FaenaService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class FaenaController {

    @Autowired
    private FaenaService faenaService;

    // http://localhost:8080/rafishdar/faenas
    @GetMapping("/faenas")
    public ResponseEntity<List<FaenaDTO>> listarTodo() {
        List<FaenaDTO> faenas = faenaService.listarTodoDTO();
        if (faenas.isEmpty()) {
            return new ResponseEntity<>(faenas, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(faenas, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/faenas
    @PostMapping("/faenas")
    public ResponseEntity<Faena> insertarFaena(@RequestBody FaenaRequestDTO request) {
        Faena newFaena = faenaService.insertarFaena(request);
        return new ResponseEntity<>(newFaena, HttpStatus.CREATED);
    }

    // http://localhost:8080/rafishdar/faenas/1
    @GetMapping("/faenas/{id}")
    public ResponseEntity<Faena> buscarPorId(@PathVariable("id") Long id) {
        Faena foundFaena = faenaService.buscarFaenaPorId(id);
        return new ResponseEntity<>(foundFaena, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/faenas/usuario/1
    @GetMapping("/faenas/usuario/{idUsuario}")
    public ResponseEntity<List<FaenaDTO>> listarPorUsuario(@PathVariable("idUsuario") Long idUsuario) {
        List<FaenaDTO> foundFaenas = faenaService.listarFaenasPorUsuarioId(idUsuario);
        if (foundFaenas.isEmpty()) {
            return new ResponseEntity<>(foundFaenas, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(foundFaenas, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/faenas/1/revertir-planificada
    @PutMapping("/faenas/{id}/revertir-planificada")
    public ResponseEntity<Faena> actualizarEstadoPlanificada(
            @PathVariable("id") Long id) {

        Faena updatedFaena =
                faenaService.cambiarEstadoAPlanificada(id);

        return new ResponseEntity<>(updatedFaena, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/faenas/1/estado/en-curso
    @PutMapping("/faenas/{id}/estado/en-curso")
    public ResponseEntity<Faena> actualizarEstadoEnCurso(
            @PathVariable("id") Long id) {

        Faena updatedFaena =
                faenaService.cambiarEstadoAEnCurso(id);

        return new ResponseEntity<>(updatedFaena, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/faenas/1/planificada
    @PutMapping("/faenas/{id}/planificada")
    public ResponseEntity<Faena> actualizarPlanificada(
            @PathVariable("id") Long id,
            @RequestBody FaenaUpdateDTO faena) {

        Faena updatedFaena =
                faenaService.actualizarPlanificada(id, faena);

        return new ResponseEntity<>(updatedFaena, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/faenas/1/en-curso
    @PutMapping("/faenas/{id}/en-curso")
    public ResponseEntity<Faena> actualizarEnCurso(
            @PathVariable("id") Long id,
            @RequestBody FaenaUpdateDTO faena) {

        Faena updatedFaena =
                faenaService.actualizarEnCurso(id, faena);

        return new ResponseEntity<>(updatedFaena, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/faenas/1/finalizar?ultimaBitacora=1
    @PutMapping("/faenas/{id}/finalizar")
    public ResponseEntity<Faena> finalizar(
            @PathVariable("id") Long id,
            @RequestParam String ultimaBitacora,
            @RequestBody BitacoraRequestDTO request) {

        Faena updatedFaena =
                faenaService.terminarFaena(id, Boolean.parseBoolean(ultimaBitacora), request);

        return new ResponseEntity<>(updatedFaena, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/faenas/1
    @DeleteMapping("/faenas/{id}")
    public ResponseEntity<HttpStatus> eliminar(
            @PathVariable("id") Long id) {

        faenaService.eliminarFaenaPlanificada(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // --- MÉTODOS ANALÍTICOS E INNOVADORES ---

    // http://localhost:8080/rafishdar/faenas/usuario/1/estado/Finalizada
    @GetMapping("/faenas/usuario/{idUsuario}/estado/{estado}")
    public ResponseEntity<List<FaenaDTO>> listarPorUsuarioYEstado(
            @PathVariable("idUsuario") Long idUsuario,
            @PathVariable("estado") String estado) {

        List<FaenaDTO> foundFaenas =
                faenaService.listarFaenasPorUsuarioYEstado(idUsuario, estado);

        if (foundFaenas.isEmpty()) {
            return new ResponseEntity<>(foundFaenas, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(foundFaenas, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/faenas/usuario/1/tiempo-promedio?inicio=2024-01-01T00:00:00&fin=2024-12-31T23:59:59
    @GetMapping("/faenas/usuario/{idUsuario}/tiempo-promedio")
    public ResponseEntity<Double> obtenerTiempoPromedio(
            @PathVariable("idUsuario") Long idUsuario,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        Double promedio =
                faenaService.obtenerTiempoPromedioFaena(
                        idUsuario,
                        inicio,
                        fin
                );

        return new ResponseEntity<>(promedio, HttpStatus.OK);
    }
}