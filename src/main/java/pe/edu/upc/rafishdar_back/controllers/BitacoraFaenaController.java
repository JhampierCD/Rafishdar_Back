package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.entities.BitacoraFaena;
import pe.edu.upc.rafishdar_back.dtos.BitacoraFaenaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.BitacoraFaenaUpdateDTO;
import pe.edu.upc.rafishdar_back.services.BitacoraFaenaService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/bitacoras")
public class BitacoraFaenaController {

    private final BitacoraFaenaService bitacoraService;

    public BitacoraFaenaController(BitacoraFaenaService bitacoraService) {
        this.bitacoraService = bitacoraService;
    }

    // http://localhost:8080/api/bitacoras
    @PostMapping
    public ResponseEntity<BitacoraFaena> insertar(@RequestBody BitacoraFaenaRequestDTO request) {
        BitacoraFaena newBitacora = bitacoraService.createBitacora(request);
        return new ResponseEntity<>(newBitacora, HttpStatus.CREATED);
    }

    // http://localhost:8080/api/bitacoras/1
    @GetMapping("/{id}")
    public ResponseEntity<BitacoraFaena> buscarPorId(@PathVariable("id") Long id) {
        BitacoraFaena foundBitacora = bitacoraService.getBitacoraById(id);
        return new ResponseEntity<>(foundBitacora, HttpStatus.OK);
    }

    // http://localhost:8080/api/bitacoras/usuario/1
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<BitacoraFaena>> listarPorUsuario(@PathVariable("idUsuario") Long idUsuario) {
        List<BitacoraFaena> foundBitacoras = bitacoraService.getBitacorasByUsuario(idUsuario);
        if (foundBitacoras.isEmpty()) {
            return new ResponseEntity<>(foundBitacoras, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(foundBitacoras, HttpStatus.OK);
    }

    // http://localhost:8080/api/bitacoras/usuario/1/estado/Finalizada
    @GetMapping("/usuario/{idUsuario}/estado/{estado}")
    public ResponseEntity<List<BitacoraFaena>> listarPorUsuarioYEstado(
            @PathVariable("idUsuario") Long idUsuario,
            @PathVariable("estado") String estado) {
        List<BitacoraFaena> foundBitacoras = bitacoraService.getBitacorasByUsuarioAndEstado(idUsuario, estado);
        if (foundBitacoras.isEmpty()) {
            return new ResponseEntity<>(foundBitacoras, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(foundBitacoras, HttpStatus.OK);
    }

    // http://localhost:8080/api/bitacoras/1/estado/en-curso
    @PutMapping("/{id}/estado/en-curso")
    public ResponseEntity<BitacoraFaena> actualizarEstadoEnCurso(@PathVariable("id") Long id) {
        BitacoraFaena updatedBitacora = bitacoraService.updateEstadoToEnCurso(id);
        return new ResponseEntity<>(updatedBitacora, HttpStatus.OK);
    }

    // http://localhost:8080/api/bitacoras/1/coordenadas?latitud=-12.04&longitud=-77.03
    @PutMapping("/{id}/coordenadas")
    public ResponseEntity<BitacoraFaena> actualizarCoordenadas(
            @PathVariable("id") Long id,
            @RequestParam Double latitud,
            @RequestParam Double longitud) {
        BitacoraFaena updatedBitacora = bitacoraService.updateCoordenadas(id, latitud, longitud);
        return new ResponseEntity<>(updatedBitacora, HttpStatus.OK);
    }

    // http://localhost:8080/api/bitacoras/1/finalizar?observaciones=Pesca%20exitosa
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<BitacoraFaena> finalizar(
            @PathVariable("id") Long id,
            @RequestParam String observaciones) {
        BitacoraFaena updatedBitacora = bitacoraService.finishBitacora(id, observaciones);
        return new ResponseEntity<>(updatedBitacora, HttpStatus.OK);
    }

    // http://localhost:8080/api/bitacoras/1
    @PutMapping("/{id}")
    public ResponseEntity<BitacoraFaena> actualizarPlanificada(
            @PathVariable("id") Long id,
            @RequestBody BitacoraFaenaUpdateDTO request) {
        BitacoraFaena updatedBitacora = bitacoraService.updateBitacoraPlanificada(id, request);
        return new ResponseEntity<>(updatedBitacora, HttpStatus.OK);
    }

    // http://localhost:8080/api/bitacoras/1
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> eliminar(@PathVariable("id") Long id) {
        bitacoraService.deleteBitacoraPlanificada(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // --- MÉTODOS ANALÍTICOS E INNOVADORES ---

    @GetMapping("/usuario/{idUsuario}/tiempo-promedio")
    public ResponseEntity<Double> obtenerTiempoPromedio(
            @PathVariable("idUsuario") Long idUsuario,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        Double promedio = bitacoraService.getTiempoPromedioFaena(idUsuario, inicio, fin);
        return new ResponseEntity<>(promedio, HttpStatus.OK);
    }

    @GetMapping("/zona/{idZona}/alerta-incidencias")
    public ResponseEntity<Boolean> verificarAlertaIncidencias(@PathVariable("idZona") Long idZona) {
        boolean hayAlerta = bitacoraService.hayAlertaDeIncidenciasEnZona(idZona);
        return new ResponseEntity<>(hayAlerta, HttpStatus.OK);
    }
}