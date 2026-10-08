package pe.edu.upc.rafishdar_back.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pe.edu.upc.rafishdar_back.entities.Authority;
import pe.edu.upc.rafishdar_back.repositories.AuthorityRepository;

@Component
public class SecurityDataInitializer implements CommandLineRunner {

    @Autowired
    AuthorityRepository authorityRepository;


    @Override
    public void run(String... args) throws Exception {

        if (authorityRepository.findByName("ADMIN") == null) {

            Authority admin = new Authority();

            admin.setName("ADMIN");

            authorityRepository.save(admin);
        }


        if (authorityRepository.findByName("PESCADOR") == null) {

            Authority pescador = new Authority();

            pescador.setName("PESCADOR");

            authorityRepository.save(pescador);
        }


        if (authorityRepository.findByName("TECNICO") == null) {

            Authority tecnico = new Authority();

            tecnico.setName("TECNICO");

            authorityRepository.save(tecnico);
        }
    }
}