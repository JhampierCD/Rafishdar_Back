package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.upc.rafishdar_back.dtos.CondicionClimaticaDTO;
import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;
import pe.edu.upc.rafishdar_back.services.CondicionClimaticaService;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class CondicionClimaticaController {

    @Autowired
    private CondicionClimaticaService condicionClimaticaService;

    @GetMapping("/condiciones-climaticas")
    public ResponseEntity<List<CondicionClimatica>> listarCondiciones() {
        List<CondicionClimatica> foundCondiciones =
                condicionClimaticaService.listarTodoCondiciones();

        if (foundCondiciones.isEmpty()) {
            return new ResponseEntity<>(
                    foundCondiciones,
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<>(
                foundCondiciones,
                HttpStatus.OK
        );
    }

    @GetMapping("/condiciones-climaticas/{id}")
    public ResponseEntity<CondicionClimatica> buscarPorId(
            @PathVariable("id") Long id) {

        CondicionClimatica foundCondicion =
                condicionClimaticaService.buscarPorId(id);

        if (foundCondicion == null) {
            return new ResponseEntity<>(
                    foundCondicion,
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                foundCondicion,
                HttpStatus.OK
        );
    }

    @PostMapping("/condiciones-climaticas/bitacoras/{idBitacora}")
    public ResponseEntity<?> insertarCondicion(
            @PathVariable("idBitacora") Long idBitacora,
            @RequestBody CondicionClimaticaDTO dto) {
        try {
            CondicionClimatica newCondicionClimatica =
                    condicionClimaticaService.insertarCondicionesPorBitacora(idBitacora, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCondicionClimatica);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/condiciones-climaticas/{id}")
    public ResponseEntity<HttpStatus> eliminarCondicion(
            @PathVariable("id") Long id) {

        if (!condicionClimaticaService.eliminarCondiciones(id)) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                HttpStatus.OK
        );
    }

    @PutMapping("/condiciones-climaticas/{id}")
    public ResponseEntity<CondicionClimatica> actualizarCondicion(
            @PathVariable("id") Long id,
            @RequestBody CondicionClimatica condicionClimatica) {

        condicionClimatica.setId(id);
        CondicionClimatica actualizada =
                condicionClimaticaService.actualizarCondiciones(condicionClimatica);
        if (actualizada == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actualizada, HttpStatus.OK);
    }

    @GetMapping("/bitacoras/{bitacoraId}/condicion-climatica")
    public ResponseEntity<?> consultarPorBitacora(
            @PathVariable Long bitacoraId,
            @RequestParam(required = false) Long usuarioId) {

        Optional<CondicionClimatica> resultado =
                (usuarioId != null)
                        ? condicionClimaticaService
                        .consultarPorBitacoraConPermisoUsuario(
                                bitacoraId,
                                usuarioId
                        )
                        : condicionClimaticaService
                        .consultarPorBitacora(
                                bitacoraId
                        );

        return resultado
                .<ResponseEntity<?>>map(
                        c -> new ResponseEntity<>(
                                c,
                                HttpStatus.OK
                        )
                )
                .orElse(
                        new ResponseEntity<>(
                                "Sin registro climático para la faena solicitada",
                                HttpStatus.NO_CONTENT
                        )
                );
    }

    @GetMapping("/condiciones-climaticas/peligrosas")
    public ResponseEntity<List<CondicionClimatica>> condicionesPeligrosas(
            @RequestParam(defaultValue = "30.0") Double umbralNudos) {

        List<CondicionClimatica> peligrosas =
                condicionClimaticaService.listarCondicionesPeligrosas(
                        umbralNudos
                );

        if (peligrosas.isEmpty()) {
            return new ResponseEntity<>(
                    peligrosas,
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<>(
                peligrosas,
                HttpStatus.OK
        );
    }

    @GetMapping("/condiciones-climaticas/promedio-temperatura")
    public ResponseEntity<?> promedioTemperaturaPorZona(
            @RequestParam Long zonaId) {

        Double promedio =
                condicionClimaticaService
                        .obtenerPromedioTemperaturaPorZona(zonaId);

        if (promedio == null) {
            return new ResponseEntity<>(
                    "Sin datos climáticos históricos para la zona indicada",
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<>(
                promedio,
                HttpStatus.OK
        );
    }
}