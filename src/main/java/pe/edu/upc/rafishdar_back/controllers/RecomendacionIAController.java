package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;
import pe.edu.upc.rafishdar_back.entities.RecomendacionIA;
import pe.edu.upc.rafishdar_back.services.RecomendacionIAService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/recomendacionesIA")
public class RecomendacionIAController {

    @Autowired
    private RecomendacionIAService recomendacionIAService;

    //http://localhost:8080/recomendacionesIA/recomendaciones-lista
    @GetMapping("/recomendaciones")
    public ResponseEntity<List<RecomendacionIA>> listar() {
        List<RecomendacionIA> foundRecomendaciones = recomendacionIAService.listarTodoRecomendaciones();
        if (foundRecomendaciones.isEmpty()) {
            return new ResponseEntity<>(foundRecomendaciones, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(foundRecomendaciones, HttpStatus.OK);
    }

    //http://localhost:8080/recomendacionesIA/recomendaciones-lista
    @GetMapping("/recomendaciones/{id}")
    public ResponseEntity<RecomendacionIA> buscarPorId(@PathVariable("id") Long id){
        RecomendacionIA foundRecomendaciones = recomendacionIAService.buscarPorId(id);
        if (foundRecomendaciones ==null){
            return new ResponseEntity<>(foundRecomendaciones, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(foundRecomendaciones, HttpStatus.OK);
    }

    //http://localhost:8080/recomendacionesIA/recomendaciones-insertar
    @PostMapping("/recomendaciones")
    public ResponseEntity<RecomendacionIA> insertar(@RequestBody RecomendacionIA recomendacionIA){
        RecomendacionIA newRecomendacionIA = recomendacionIAService.insertarRecomendaciones(recomendacionIA);

        if (newRecomendacionIA ==null){
            return new ResponseEntity<>(newRecomendacionIA, HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(newRecomendacionIA, HttpStatus.CREATED);
    }

    //http://localhost:8080/recomendacionesIA/recomendaciones-eliminar
    @DeleteMapping("/recomendaciones/{id}")
    public ResponseEntity<HttpStatus> eliminar(@PathVariable("id") Long id){
        if(!recomendacionIAService.eliminarRecomendaciones(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        };
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //http://localhost:8080/recomendacionesIA/recomendaciones-actualizar
    @PutMapping("/eventos")
    public ResponseEntity<RecomendacionIA> actualizar(@RequestBody RecomendacionIA recomendacionIA){
        if(recomendacionIAService.actualizarRecomendaciones(recomendacionIA)==null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        };
        return new ResponseEntity<>(recomendacionIA,HttpStatus.OK);
    }
}
