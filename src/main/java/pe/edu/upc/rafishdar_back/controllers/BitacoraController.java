package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraDTO;
import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.Bitacora;
import pe.edu.upc.rafishdar_back.services.BitacoraService;
import pe.edu.upc.rafishdar_back.services.FaenaService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class BitacoraController {
    @Autowired
    private BitacoraService bitacoraService;

    // http://localhost:8080/rafishdar/bitacoras
    @GetMapping("/bitacoras")
    public ResponseEntity<List<BitacoraDTO>> listarTodo() {
        List<BitacoraDTO> bitacoras = bitacoraService.listarTodoDTO();
        if (bitacoras.isEmpty()) {
            return new ResponseEntity<>(bitacoras, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(bitacoras, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/bitacoras
    @PostMapping("/bitacoras")
    public ResponseEntity<Bitacora> insertar(@RequestBody BitacoraRequestDTO request) {
        Bitacora newBitacora = bitacoraService.insertarBitacora(request);
        return new ResponseEntity<>(newBitacora, HttpStatus.CREATED);
    }

    // http://localhost:8080/rafishdar/bitacoras/1
    @GetMapping("/bitacoras/{id}")
    public ResponseEntity<Bitacora> buscarPorId(@PathVariable("id") Long id) {
        Bitacora foundBitacora = bitacoraService.buscarBitacoraPorId(id);
        return new ResponseEntity<>(foundBitacora, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/bitacoras/1
    @PutMapping("/bitacoras/{id}")
    public ResponseEntity<Bitacora> actualizar(
            @PathVariable("id") Long id,
            @RequestBody BitacoraUpdateDTO bitacora) {

        Bitacora updatedBitacora =
                bitacoraService.actualizarBitacora(bitacora);

        return new ResponseEntity<>(updatedBitacora, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/bitacoras/1
    @DeleteMapping("/bitacoras/{id}")
    public ResponseEntity<HttpStatus> eliminar(
            @PathVariable("id") Long id) {

        bitacoraService.eliminarBitacora(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // --- MÉTODOS ANALÍTICOS E INNOVADORES ---

    // http://localhost:8080/rafishdar/bitacoras/1/alerta-desvio
    @GetMapping("/bitacoras/{id}/alerta-desvio")
    public ResponseEntity<Boolean> verificarDesvio(
            @PathVariable("id") Long id) {

        boolean hayDesvio =
                bitacoraService.verificarDesvioDeZona(id);

        return new ResponseEntity<>(hayDesvio, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/bitacoras/zona/1/alerta-incidencias?keyword=alerta
    @GetMapping("/bitacoras/zona/{idZona}/alerta-incidencias")
    public ResponseEntity<Boolean> verificarAlertaIncidencias(
            @PathVariable("idZona") Long idZona,
            @RequestParam String keyword) {

        boolean hayAlerta =
                bitacoraService.hayAlertaDeIncidenciasEnZona(idZona, keyword);

        return new ResponseEntity<>(hayAlerta, HttpStatus.OK);
    }
}
