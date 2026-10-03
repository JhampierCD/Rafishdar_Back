package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //http://localhost:8080/rafishdar/condiciones-climaticas-lista
    @GetMapping("/condiciones")
    public ResponseEntity<List<CondicionClimatica>> listar() {
        List<CondicionClimatica> foundCondiciones = condicionClimaticaService.listarTodoCondiciones();
        if (foundCondiciones.isEmpty()) {
            return new ResponseEntity<>(foundCondiciones, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(foundCondiciones, HttpStatus.OK);
    }

    //http://localhost:8080/rafishdar/condiciones-ids
    @GetMapping("/condiciones/{id}")
    public ResponseEntity<CondicionClimatica> buscarPorId(@PathVariable("id") Long id){
        CondicionClimatica foundCondicion = condicionClimaticaService.buscarPorId(id);
        if (foundCondicion ==null){
            return new ResponseEntity<>(foundCondicion, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(foundCondicion, HttpStatus.OK);
    }

    //http://localhost:8080/rafishdar/condiciones-climaticas-lista
    @PostMapping("/condiciones")
    public ResponseEntity<CondicionClimatica> insertar(@RequestBody CondicionClimatica condicionClimatica){
        CondicionClimatica newCondicionClimatica = condicionClimaticaService.insertarCondiciones(condicionClimatica);

        if (newCondicionClimatica==null){
            return new ResponseEntity<>(newCondicionClimatica, HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(newCondicionClimatica, HttpStatus.CREATED);
    }

    //http://localhost:8080/rafishdar/condiciones-ids
    @DeleteMapping("/condiciones/{id}")
    public ResponseEntity<HttpStatus> eliminar(@PathVariable("id") Long id){
        if(!condicionClimaticaService.eliminarCondiciones(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        };
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //http://localhost:8080/rafishdar/condiciones-ids
    @PutMapping("/condiciones/{id}")
    public ResponseEntity<CondicionClimatica> actualizar(@RequestBody CondicionClimatica condicionClimatica){
        if(condicionClimaticaService.actualizarCondiciones(condicionClimatica)==null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        };
        return new ResponseEntity<>(condicionClimatica,HttpStatus.OK);
    }


    // US-12, BN-12: consulta condición de una bitácora validando propietario
    @GetMapping("/bitacoras/{bitacoraId}/condicion-climatica")
    public ResponseEntity<?> consultarPorBitacora(
            @PathVariable Long bitacoraId,
            @RequestParam(required = false) Long usuarioId) {

        Optional<CondicionClimatica> resultado = (usuarioId != null)
                ? condicionClimaticaService.consultarPorBitacoraConPermisoUsuario(bitacoraId, usuarioId)
                : condicionClimaticaService.consultarPorBitacora(bitacoraId);

        // US-12 criterio: si no existe se muestra estado explícito, no error
        return resultado.<ResponseEntity<?>>map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElse(new ResponseEntity<>("Sin registro climático para la faena solicitada",
                        HttpStatus.NO_CONTENT)
        );
    }

    // US-11: alerta de condiciones peligrosas
// GET /rafishdar/condiciones/peligrosas?umbralNudos=30
    @GetMapping("/condiciones/peligrosas")
    public ResponseEntity<List<CondicionClimatica>> condicionesPeligrosas(
            @RequestParam(defaultValue = "30.0") Double umbralNudos) {
        List<CondicionClimatica> peligrosas =
                condicionClimaticaService.listarCondicionesPeligrosas(umbralNudos);
        if (peligrosas.isEmpty()) {
            return new ResponseEntity<>(peligrosas, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(peligrosas, HttpStatus.OK);
    }

    // US-34 Dashboard: promedio de temperatura por zona
// GET /rafishdar/condiciones/promedio-temperatura?zonaId=X
    @GetMapping("/condiciones/promedio-temperatura")
    public ResponseEntity<?> promedioTemperaturaPorZona(@RequestParam Long zonaId) {
        Double promedio = condicionClimaticaService.obtenerPromedioTemperaturaPorZona(zonaId);
        if (promedio == null) {
            return new ResponseEntity<>(
                    "Sin datos climáticos históricos para la zona indicada",
                    HttpStatus.NO_CONTENT
            );
        }
        return new ResponseEntity<>(promedio, HttpStatus.OK);
    }
}
