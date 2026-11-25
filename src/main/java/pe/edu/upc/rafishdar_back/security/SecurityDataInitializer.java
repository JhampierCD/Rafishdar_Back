package pe.edu.upc.rafishdar_back.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pe.edu.upc.rafishdar_back.entities.Authority;
import pe.edu.upc.rafishdar_back.repositories.AuthorityRepository;

@Component
@Order(1)
public class SecurityDataInitializer implements CommandLineRunner {

    @Autowired
    AuthorityRepository authorityRepository;


    @Override
    public void run(String... args) throws Exception {

        if (authorityRepository.findFirstByNameOrderByIdAsc("ADMIN") == null) {

            Authority admin = new Authority();

            admin.setName("ADMIN");

            authorityRepository.save(admin);
        }


        if (authorityRepository.findFirstByNameOrderByIdAsc("PESCADOR") == null) {

            Authority pescador = new Authority();

            pescador.setName("PESCADOR");

            authorityRepository.save(pescador);
        }


        if (authorityRepository.findFirstByNameOrderByIdAsc("TECNICO") == null) {

            Authority tecnico = new Authority();

            tecnico.setName("TECNICO");

            authorityRepository.save(tecnico);
        }
    }
}