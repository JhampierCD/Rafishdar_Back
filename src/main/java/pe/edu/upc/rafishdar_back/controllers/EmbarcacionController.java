package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.entities.Embarcacion;
import pe.edu.upc.rafishdar_back.services.EmbarcacionService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/embarcaciones")
public class EmbarcacionController {
    @Autowired
    EmbarcacionService embarcacionService;

    @GetMapping("/listar")
    public ResponseEntity<List<Embarcacion>> listarEmbarcacion(){
        List<Embarcacion> foundEmbarcaciones = embarcacionService.listarEmbarcaciones();
        if (foundEmbarcaciones.isEmpty()){
            return new ResponseEntity<>(foundEmbarcaciones, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(foundEmbarcaciones, HttpStatus.OK);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Embarcacion> buscarporId(@PathVariable("id") Long id){
        Embarcacion foundEmbarcacion = embarcacionService.buscarPorId(id);
        if (foundEmbarcacion == null){
            return new ResponseEntity<>(foundEmbarcacion, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(foundEmbarcacion, HttpStatus.OK);
    }

    @PostMapping("/insertarembarcacion")
    public ResponseEntity<Embarcacion> insertar(@RequestBody Embarcacion embarcacion){
        Embarcacion newEmbarcacion = embarcacionService.insertar(embarcacion);
        if (newEmbarcacion == null){
            return new ResponseEntity<>(newEmbarcacion, HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(newEmbarcacion, HttpStatus.CREATED);
    }
    @DeleteMapping("/eliminarembarcacion")
    public ResponseEntity<HttpStatus> eliminar(@PathVariable("id") Long id){
        if (!embarcacionService.eliminar(id)){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/actualizarembarcacion")
    public ResponseEntity<Embarcacion> actualizar(@RequestBody Embarcacion embarcacion){
        if( embarcacionService.actualizar(embarcacion) == null){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(embarcacion, HttpStatus.OK);
    }
}
