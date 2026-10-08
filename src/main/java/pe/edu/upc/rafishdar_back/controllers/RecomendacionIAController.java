package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;
import pe.edu.upc.rafishdar_back.entities.RecomendacionIA;
import pe.edu.upc.rafishdar_back.services.RecomendacionIAService;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class RecomendacionIAController {

    @Autowired
    private RecomendacionIAService recomendacionIAService;

    //http://localhost:8080/rafishdar/recomendaciones-ia-lista
    @GetMapping("/recomendaciones-ia")
    public ResponseEntity<List<RecomendacionIA>> listar() {

        List<RecomendacionIA> foundRecomendaciones =
                recomendacionIAService.listarTodoRecomendaciones();

        if (foundRecomendaciones.isEmpty()) {
            return new ResponseEntity<>(
                    foundRecomendaciones,
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<>(
                foundRecomendaciones,
                HttpStatus.OK
        );
    }

    //http://localhost:8080/rafishdar/recomendaciones-ia-ids
    @GetMapping("/recomendaciones-ia/{id}")
    public ResponseEntity<RecomendacionIA> buscarPorId(
            @PathVariable("id") Long id) {

        RecomendacionIA foundRecomendaciones =
                recomendacionIAService.buscarPorId(id);

        if (foundRecomendaciones == null) {
            return new ResponseEntity<>(
                    foundRecomendaciones,
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                foundRecomendaciones,
                HttpStatus.OK
        );
    }

    //http://localhost:8080/rafishdar/recomendaciones-ia-lista
    @PostMapping("/recomendaciones-ia")
    public ResponseEntity<RecomendacionIA> insertar(
            @RequestBody RecomendacionIA recomendacionIA) {

        RecomendacionIA newRecomendacionIA =
                recomendacionIAService
                        .insertarRecomendaciones(recomendacionIA);

        if (newRecomendacionIA == null) {
            return new ResponseEntity<>(
                    newRecomendacionIA,
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                newRecomendacionIA,
                HttpStatus.CREATED
        );
    }

    //http://localhost:8080/rafishdar/recomendaciones-ia-ids
    @DeleteMapping("/recomendaciones-ia/{id}")
    public ResponseEntity<HttpStatus> eliminar(
            @PathVariable("id") Long id) {

        if (!recomendacionIAService.eliminarRecomendaciones(id)) {
            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                HttpStatus.OK
        );
    }

    //http://localhost:8080/rafishdar/recomendaciones-ia-ids
    @PutMapping("/recomendaciones-ia")
    public ResponseEntity<RecomendacionIA> actualizar(
            @RequestBody RecomendacionIA recomendacionIA) {

        if (recomendacionIAService
                .actualizarRecomendaciones(recomendacionIA) == null) {

            return new ResponseEntity<>(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        return new ResponseEntity<>(
                recomendacionIA,
                HttpStatus.OK
        );
    }

    // US-24: generar recomendación por primera vez
    // POST /rafishdar/recomendaciones-ia/bitacoras/{bitacoraId}?usuarioId=X
    @PostMapping("/recomendaciones-ia/bitacoras/{bitacoraId}")
    public ResponseEntity<?> generar(
            @PathVariable Long bitacoraId,
            @RequestParam Long usuarioId) {

        try {

            RecomendacionIA nueva =
                    recomendacionIAService
                            .generarParaBitacora(
                                    bitacoraId,
                                    usuarioId
                            );

            return new ResponseEntity<>(
                    nueva,
                    HttpStatus.CREATED
            );

        } catch (IllegalStateException e) {

            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.CONFLICT
            );

        } catch (IllegalArgumentException | SecurityException e) {

            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // US-26: regenerar recomendación ante cambios en la faena
    // PUT /rafishdar/recomendaciones-ia/bitacoras/{bitacoraId}?usuarioId=X
    @PutMapping("/recomendaciones-ia/bitacoras/{bitacoraId}")
    public ResponseEntity<?> regenerar(
            @PathVariable Long bitacoraId,
            @RequestParam Long usuarioId) {

        try {

            RecomendacionIA actualizada =
                    recomendacionIAService
                            .regenerarParaBitacora(
                                    bitacoraId,
                                    usuarioId
                            );

            return new ResponseEntity<>(
                    actualizada,
                    HttpStatus.OK
            );

        } catch (IllegalArgumentException | SecurityException e) {

            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // US-25: consultar recomendación de una bitácora validando propietario
    // GET /rafishdar/recomendaciones-ia/bitacoras/{bitacoraId}?usuarioId=X
    @GetMapping("/recomendaciones-ia/bitacoras/{bitacoraId}")
    public ResponseEntity<?> consultar(
            @PathVariable Long bitacoraId,
            @RequestParam(required = false) Long usuarioId) {

        Optional<RecomendacionIA> resultado =
                (usuarioId != null)
                        ? recomendacionIAService
                        .consultarPorBitacoraConPermisoUsuario(
                                bitacoraId,
                                usuarioId
                        )
                        : recomendacionIAService
                        .consultarPorBitacora(
                                bitacoraId
                        );

        // US-25 criterio: si no existe mostrar estado vacío claro
        return resultado
                .<ResponseEntity<?>>map(
                        r -> new ResponseEntity<>(
                                r,
                                HttpStatus.OK
                        )
                )
                .orElse(
                        new ResponseEntity<>(
                                "Sin recomendación generada para esta faena",
                                HttpStatus.NO_CONTENT
                        )
                );
    }

    // US-33 Técnico: recomendaciones con nivel ALTO recientes
    // GET /rafishdar/recomendaciones-ia/alto-riesgo?horas=24
    @GetMapping("/recomendaciones-ia/alto-riesgo")
    public ResponseEntity<List<RecomendacionIA>> altoRiesgoRecientes(
            @RequestParam(defaultValue = "24") int horas) {

        List<RecomendacionIA> resultado =
                recomendacionIAService
                        .listarAltoRiesgoRecientes(horas);

        if (resultado.isEmpty()) {
            return new ResponseEntity<>(
                    resultado,
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<>(
                resultado,
                HttpStatus.OK
        );
    }
}