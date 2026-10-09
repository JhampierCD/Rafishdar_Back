package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.rafishdar_back.entities.Authority;
import pe.edu.upc.rafishdar_back.services.AuthorityService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class AuthorityController {

    @Autowired
    AuthorityService authorityService;

    @GetMapping("/authorities")
    public ResponseEntity<List<Authority>> listarTodo() {
        List<Authority> authorities = authorityService.listarTodo();
        if (authorities.isEmpty()) {
            return new ResponseEntity<>(authorities, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(authorities, HttpStatus.OK);
    }

    @GetMapping("/authorities/user/{userId}")
    public ResponseEntity<List<Authority>> listarPorUsuario(
            @PathVariable Long userId) {
        List<Authority> authorities = authorityService.listarPorUsuario(userId);
        if (authorities.isEmpty()) {
            return new ResponseEntity<>(authorities, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(authorities, HttpStatus.OK);
    }
}