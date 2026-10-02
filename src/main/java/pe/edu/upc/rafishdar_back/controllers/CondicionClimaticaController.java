package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;
import pe.edu.upc.rafishdar_back.services.CondicionClimaticaService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/condiciones-climaticas")
public class CondicionClimaticaController {
    @Autowired
    private CondicionClimaticaService condicionClimaticaService;

    //http://localhost:8080/condiciones-climaticas/condiciones-lista
    @GetMapping("/condiciones")
    public ResponseEntity<List<CondicionClimatica>> listar() {
        List<CondicionClimatica> foundCondiciones = condicionClimaticaService.listarTodoCondiciones();
        if (foundCondiciones.isEmpty()) {
            return new ResponseEntity<>(foundCondiciones, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(foundCondiciones, HttpStatus.OK);
    }

    //http://localhost:8080/condiciones-climaticas/condiciones-lista
    @GetMapping("/condiciones/{id}")
    public ResponseEntity<CondicionClimatica> buscarPorId(@PathVariable("id") Long id){
        CondicionClimatica foundCondicion = condicionClimaticaService.buscarPorId(id);
        if (foundCondicion ==null){
            return new ResponseEntity<>(foundCondicion, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(foundCondicion, HttpStatus.OK);
    }

    //http://localhost:8080/condiciones-climaticas/condiciones-insertar
    @PostMapping("/condiciones")
    public ResponseEntity<CondicionClimatica> insertar(@RequestBody CondicionClimatica condicionClimatica){
        CondicionClimatica newCondicionClimatica = condicionClimaticaService.insertarCondiciones(condicionClimatica);

        if (newCondicionClimatica==null){
            return new ResponseEntity<>(newCondicionClimatica, HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(newCondicionClimatica, HttpStatus.CREATED);
    }

    //http://localhost:8080/condiciones-climaticas/condiciones-eliminar
    @DeleteMapping("/eventos/{id}")
    public ResponseEntity<HttpStatus> eliminar(@PathVariable("id") Long id){
        if(!condicionClimaticaService.eliminarCondiciones(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        };
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //http://localhost:8080/condiciones-climaticas/condiciones-actualizar
    @PutMapping("/eventos")
    public ResponseEntity<CondicionClimatica> actualizar(@RequestBody CondicionClimatica condicionClimatica){
        if(condicionClimaticaService.actualizarCondiciones(condicionClimatica)==null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        };
        return new ResponseEntity<>(condicionClimatica,HttpStatus.OK);
    }
}
