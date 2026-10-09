package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.entities.Authority;
import pe.edu.upc.rafishdar_back.repositories.AuthorityRepository;
import pe.edu.upc.rafishdar_back.services.AuthorityService;

import java.util.List;

@Service
public class AuthorityServiceImpl
        implements AuthorityService {

    @Autowired
    AuthorityRepository authorityRepository;

    @Override
    public Authority insertar(
            Authority authority) {

        return authorityRepository.save(authority);
    }

    @Override
    public List<Authority> listarTodo() {
        return authorityRepository.findAll();
    }

    @Override
    public List<Authority> listarPorUsuario(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return authorityRepository.findByUserId(userId);
    }
}