package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.rafishdar_back.services.AuthorityService;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar")
public class AuthorityController {

    @Autowired
    AuthorityService authorityService;
}