package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.entities.GastoOperativo;
import pe.edu.upc.rafishdar_back.dtos.GastoOperativoRequestDTO;
import pe.edu.upc.rafishdar_back.services.GastoOperativoService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class GastoOperativoController {

    @Autowired
    private GastoOperativoService gastoService;

    @GetMapping("/gastos-operativos")
    public ResponseEntity<List<GastoOperativo>> listarTodo() {
        List<GastoOperativo> gastos = gastoService.listarTodo();
        if (gastos.isEmpty()) {
            return new ResponseEntity<>(gastos, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(gastos, HttpStatus.OK);
    }

    @PostMapping("/gastos-operativos/bitacoras/{idBitacora}/gasto")
    public ResponseEntity<GastoOperativo> insertar(
            @PathVariable("idBitacora") Long idBitacora,
            @RequestBody GastoOperativoRequestDTO request) {

        GastoOperativo newGasto =
                gastoService.insertarGasto(
                        idBitacora,
                        request
                );

        return new ResponseEntity<>(
                newGasto,
                HttpStatus.CREATED
        );
    }

    @PutMapping(
            "/gastos-operativos/bitacoras/{idBitacora}/gasto"
    )
    public ResponseEntity<GastoOperativo> actualizar(
            @PathVariable("idBitacora") Long idBitacora,
            @RequestBody GastoOperativoRequestDTO request) {

        GastoOperativo updatedGasto =
                gastoService.actualizarGasto(
                        idBitacora,
                        request
                );

        return new ResponseEntity<>(
                updatedGasto,
                HttpStatus.OK
        );
    }

    @GetMapping(
            "/gastos-operativos/bitacoras/{idBitacora}/gasto"
    )
    public ResponseEntity<GastoOperativo> buscarPorBitacora(
            @PathVariable("idBitacora") Long idBitacora) {

        GastoOperativo foundGasto =
                gastoService.buscarGastoPorBitacora(
                        idBitacora
                );

        return new ResponseEntity<>(
                foundGasto,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/gastos-operativos/bitacoras/{idBitacora}/gasto")
    public ResponseEntity<Void> eliminar(
            @PathVariable("idBitacora") Long idBitacora) {
        gastoService.eliminar(idBitacora);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(
            "/gastos-operativos/total/usuario/{idUsuario}"
    )
    public ResponseEntity<Double> obtenerTotalGastos(
            @PathVariable("idUsuario") Long idUsuario,
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime inicio,
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime fin) {

        Double total =
                gastoService.obtenerTotalGastosPorRango(
                        idUsuario,
                        inicio,
                        fin
                );

        return new ResponseEntity<>(
                total,
                HttpStatus.OK
        );
    }

    @GetMapping(
            "/gastos-operativos/promedio/usuario/{idUsuario}"
    )
    public ResponseEntity<Double> obtenerPromedioGastos(
            @PathVariable("idUsuario") Long idUsuario,
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime inicio,
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime fin) {

        Double promedio = gastoService.obtenerPromedioGastosPorRango(
                        idUsuario,
                        inicio,
                        fin
                );

        return new ResponseEntity<>(
                promedio,
                HttpStatus.OK
        );
    }

    @GetMapping(
            "/gastos-operativos/eficiencia/zona/{idZona}"
    )
    public ResponseEntity<Double> obtenerEficienciaCombustible(
            @PathVariable("idZona") Long idZona,
            @RequestParam int diasAtras) {

        Double eficiencia =
                gastoService.obtenerEficienciaCombustiblePorZona(
                        idZona,
                        diasAtras
                );

        return new ResponseEntity<>(
                eficiencia,
                HttpStatus.OK
        );
    }
}