package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.entities.DetalleCaptura;
import pe.edu.upc.rafishdar_back.dtos.DetalleCapturaRequestDTO;
import pe.edu.upc.rafishdar_back.services.DetalleCapturaService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DetalleCapturaController {

    private final DetalleCapturaService capturaService;

    public DetalleCapturaController(DetalleCapturaService capturaService) {
        this.capturaService = capturaService;
    }

    // http://localhost:8080/api/bitacoras/1/capturas
    @PostMapping("/bitacoras/{idBitacora}/capturas")
    public ResponseEntity<DetalleCaptura> insertar(
            @PathVariable("idBitacora") Long idBitacora,
            @RequestBody DetalleCapturaRequestDTO request) {
        DetalleCaptura newCaptura = capturaService.insertarCaptura(idBitacora, request);
        return new ResponseEntity<>(newCaptura, HttpStatus.CREATED);
    }

    // http://localhost:8080/api/capturas/1?nuevoVolumen=50.5
    @PutMapping("/capturas/{idDetalle}")
    public ResponseEntity<DetalleCaptura> actualizar(
            @PathVariable("idDetalle") Long idDetalle,
            @RequestParam Double nuevoVolumen) {
        DetalleCaptura updatedCaptura = capturaService.actualizarCaptura(idDetalle, nuevoVolumen);
        return new ResponseEntity<>(updatedCaptura, HttpStatus.OK);
    }

    // http://localhost:8080/api/capturas/1
    @DeleteMapping("/capturas/{idDetalle}")
    public ResponseEntity<HttpStatus> eliminar(@PathVariable("idDetalle") Long idDetalle) {
        capturaService.eliminarCaptura(idDetalle);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // http://localhost:8080/api/bitacoras/1/capturas
    @GetMapping("/bitacoras/{idBitacora}/capturas")
    public ResponseEntity<List<DetalleCaptura>> listarPorBitacora(@PathVariable("idBitacora") Long idBitacora) {
        List<DetalleCaptura> foundCapturas = capturaService.listarCapturasPorBitacora(idBitacora);
        if (foundCapturas.isEmpty()) {
            return new ResponseEntity<>(foundCapturas, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(foundCapturas, HttpStatus.OK);
    }

    // --- MÉTODOS ANALÍTICOS E INNOVADORES ---

    @GetMapping("/capturas/volumen-total/usuario/{idUsuario}")
    public ResponseEntity<Double> obtenerVolumenTotal(
            @PathVariable("idUsuario") Long idUsuario,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        Double total = capturaService.obtenerVolumenTotalPorRango(idUsuario, inicio, fin);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }

    @GetMapping("/capturas/volumen-especie/usuario/{idUsuario}")
    public ResponseEntity<Double> obtenerVolumenPorEspecie(
            @PathVariable("idUsuario") Long idUsuario,
            @RequestParam Long idEspecie,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        Double total = capturaService.obtenerVolumenTotalPorEspecieYRango(idUsuario, idEspecie, inicio, fin);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }

    @GetMapping("/capturas/top-zonas/especie/{idEspecie}")
    public ResponseEntity<List<Long>> listarTopZonasParaEspecie(@PathVariable("idEspecie") Long idEspecie) {
        List<Long> foundZonas = capturaService.obtenerTopZonasParaEspecieEnMesCorriente(idEspecie);
        if (foundZonas.isEmpty()) {
            return new ResponseEntity<>(foundZonas, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(foundZonas, HttpStatus.OK);
    }

    @GetMapping("/bitacoras/{idBitacora}/capturas/porcentaje-incidental")
    public ResponseEntity<Double> obtenerPorcentajeIncidental(@PathVariable("idBitacora") Long idBitacora) {
        Double porcentaje = capturaService.obtenerPorcentajeCapturaIncidental(idBitacora);
        return new ResponseEntity<>(porcentaje, HttpStatus.OK);
    }
}