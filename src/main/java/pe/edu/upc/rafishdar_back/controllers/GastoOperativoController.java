package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.entities.GastoOperativo;
import pe.edu.upc.rafishdar_back.dtos.gasto_operativo.GastoOperativoRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.gasto_operativo.GastoOperativoUpdateDTO;
import pe.edu.upc.rafishdar_back.services.GastoOperativoService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class GastoOperativoController {

    @Autowired
    private GastoOperativoService gastoService;

    // http://localhost:8080/rafishdar/gastos-operativos
    @GetMapping("/gastos-operativos")
    public ResponseEntity<List<GastoOperativo>> listarTodo() {
        List<GastoOperativo> gastos = gastoService.listarTodo();
        if (gastos.isEmpty()) {
            return new ResponseEntity<>(gastos, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(gastos, HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/gastos-operativos/faenas/1/gasto
    @PostMapping("/gastos-operativos/faenas/{idFaena}/gasto")
    public ResponseEntity<GastoOperativo> insertar(
            @PathVariable("idFaena") Long idFaena,
            @RequestBody GastoOperativoRequestDTO request) {

        GastoOperativo newGasto =
                gastoService.insertarGasto(
                        idFaena,
                        request
                );

        return new ResponseEntity<>(
                newGasto,
                HttpStatus.CREATED
        );
    }

    // http://localhost:8080/rafishdar/gastos-operativos/faenas/1/gasto
    @PutMapping(
            "/gastos-operativos/faenas/{idFaena}/gasto"
    )
    public ResponseEntity<GastoOperativo> actualizar(
            @PathVariable("idFaena") Long idFaena,
            @RequestBody GastoOperativoUpdateDTO request) {

        GastoOperativo updatedGasto =
                gastoService.actualizarGasto(
                        idFaena,
                        request
                );

        return new ResponseEntity<>(
                updatedGasto,
                HttpStatus.OK
        );
    }

    // http://localhost:8080/rafishdar/gastos-operativos/bitacoras/1/gasto
    @GetMapping(
            "/gastos-operativos/faenas/{idFaena}/gasto"
    )
    public ResponseEntity<GastoOperativo> buscarPorFaena(
            @PathVariable("idFaena") Long idFaena) {

        GastoOperativo foundGasto =
                gastoService.buscarGastoPorFaena(
                        idFaena
                );

        return new ResponseEntity<>(
                foundGasto,
                HttpStatus.OK
        );
    }

    // http://localhost:8080/rafishdar/gastos-operativos/faenas/1/gasto
    @DeleteMapping("/gastos-operativos/faenas/{idFaena}/gasto")
    public ResponseEntity<Boolean> eliminar(
            @PathVariable("idFaena") Long idFaena) {
        return new ResponseEntity<>(gastoService.eliminar(idFaena), HttpStatus.OK);
    }

    // http://localhost:8080/rafishdar/gastos-operativos/total/usuario/1?inicio=2024-01-01T00:00:00&fin=2024-12-31T23:59:59
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

    // http://localhost:8080/rafishdar/gastos-operativos/promedio/usuario/1?inicio=2024-01-01T00:00:00&fin=2024-12-31T23:59:59
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

        Double promedio =
                gastoService.obtenerPromedioGastosPorRango(
                        idUsuario,
                        inicio,
                        fin
                );

        return new ResponseEntity<>(
                promedio,
                HttpStatus.OK
        );
    }

    // http://localhost:8080/rafishdar/gastos-operativos/eficiencia/zona/1?diasAtras=30
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