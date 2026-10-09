package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.entities.BitacoraFaena;
import pe.edu.upc.rafishdar_back.services.BitacoraFaenaService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class BitacoraFaenaController {

    @Autowired
    private BitacoraFaenaService bitacoraService;

    @GetMapping("/bitacoras")
    public ResponseEntity<List<BitacoraFaena>> listarTodo() {
        List<BitacoraFaena> bitacoras = bitacoraService.listarTodo();
        if (bitacoras.isEmpty()) {
            return new ResponseEntity<>(bitacoras, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(bitacoras, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/bitacoras
    @PostMapping("/bitacoras")
    public ResponseEntity<BitacoraFaena> insertarBitacora(@RequestBody BitacoraFaena request) {
        BitacoraFaena newBitacora = bitacoraService.insertarBitacora(request);
        return new ResponseEntity<>(newBitacora, HttpStatus.CREATED);
    }

    // http://localhost:8080/rafishdar/bitacoras/1
    @GetMapping("/bitacoras/{id}")
    public ResponseEntity<BitacoraFaena> buscarPorId(@PathVariable("id") Long id) {
        BitacoraFaena foundBitacora = bitacoraService.buscarBitacoraPorId(id);
        return new ResponseEntity<>(foundBitacora, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/bitacoras/usuario/1
    @GetMapping("/bitacoras/usuario/{idUsuario}")
    public ResponseEntity<List<BitacoraFaena>> listarPorUsuario(@PathVariable("idUsuario") Long idUsuario) {
        List<BitacoraFaena> foundBitacoras = bitacoraService.listarBitacorasPorUsuarioId(idUsuario);
        if (foundBitacoras.isEmpty()) {
            return new ResponseEntity<>(foundBitacoras, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(foundBitacoras, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/bitacoras/usuario/1/estado/Finalizada
    @GetMapping("/bitacoras/usuario/{idUsuario}/estado/{estado}")
    public ResponseEntity<List<BitacoraFaena>> listarPorUsuarioYEstado(
            @PathVariable("idUsuario") Long idUsuario,
            @PathVariable("estado") String estado) {

        List<BitacoraFaena> foundBitacoras =
                bitacoraService.listarBitacorasPorUsuarioYEstado(idUsuario, estado);

        if (foundBitacoras.isEmpty()) {
            return new ResponseEntity<>(foundBitacoras, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(foundBitacoras, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/bitacoras/1/revertir-planificada
    @PutMapping("/bitacoras/{id}/revertir-planificada")
    public ResponseEntity<BitacoraFaena> actualizarEstadoPlanificada(
            @PathVariable("id") Long id) {

        BitacoraFaena updatedBitacora =
                bitacoraService.cambiarEstadoAPlanificada(id);

        return new ResponseEntity<>(updatedBitacora, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/bitacoras/1/estado/en-curso
    @PutMapping("/bitacoras/{id}/estado/en-curso")
    public ResponseEntity<BitacoraFaena> actualizarEstadoEnCurso(
            @PathVariable("id") Long id) {

        BitacoraFaena updatedBitacora =
                bitacoraService.cambiarEstadoAEnCurso(id);

        return new ResponseEntity<>(updatedBitacora, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/bitacoras/1/planificada
    @PutMapping("/bitacoras/{id}/planificada")
    public ResponseEntity<BitacoraFaena> actualizarPlanificada(
            @PathVariable("id") Long id,
            @RequestBody BitacoraFaena bitacora) {

        BitacoraFaena updatedBitacora =
                bitacoraService.actualizarPlanificada(id, bitacora);

        return new ResponseEntity<>(updatedBitacora, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/bitacoras/1/en-curso
    @PutMapping("/bitacoras/{id}/en-curso")
    public ResponseEntity<BitacoraFaena> actualizarEnCurso(
            @PathVariable("id") Long id,
            @RequestBody BitacoraFaena bitacora) {

        BitacoraFaena updatedBitacora =
                bitacoraService.actualizarEnCurso(id, bitacora);

        return new ResponseEntity<>(updatedBitacora, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/bitacoras/1/finalizar?observaciones=Pesca%20exitosa
    @PutMapping("/bitacoras/{id}/finalizar")
    public ResponseEntity<BitacoraFaena> finalizar(
            @PathVariable("id") Long id,
            @RequestParam String observaciones) {

        BitacoraFaena updatedBitacora =
                bitacoraService.terminarBitacora(id, observaciones);

        return new ResponseEntity<>(updatedBitacora, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/bitacoras/1
    @DeleteMapping("/bitacoras/{id}")
    public ResponseEntity<HttpStatus> eliminar(
            @PathVariable("id") Long id) {

        bitacoraService.eliminarBitacoraPlanificada(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // --- MÉTODOS ANALÍTICOS E INNOVADORES ---

    // http://localhost:8080/rafishdar/bitacoras/usuario/1/tiempo-promedio?inicio=2024-01-01T00:00:00&fin=2024-12-31T23:59:59
    @GetMapping("/bitacoras/usuario/{idUsuario}/tiempo-promedio")
    public ResponseEntity<Double> obtenerTiempoPromedio(
            @PathVariable("idUsuario") Long idUsuario,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        Double promedio =
                bitacoraService.obtenerTiempoPromedioFaena(
                        idUsuario,
                        inicio,
                        fin
                );

        return new ResponseEntity<>(promedio, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/bitacoras/1/alerta-desvio
    @GetMapping("/bitacoras/{id}/alerta-desvio")
    public ResponseEntity<Boolean> verificarDesvio(
            @PathVariable("id") Long id) {

        boolean hayDesvio =
                bitacoraService.verificarDesvioDeZona(id);

        return new ResponseEntity<>(hayDesvio, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/bitacoras/zona/1/alerta-incidencias
    @GetMapping("/bitacoras/zona/{idZona}/alerta-incidencias")
    public ResponseEntity<Boolean> verificarAlertaIncidencias(
            @PathVariable("idZona") Long idZona) {

        boolean hayAlerta =
                bitacoraService.hayAlertaDeIncidenciasEnZona(idZona);

        return new ResponseEntity<>(hayAlerta, HttpStatus.OK);
    }
}