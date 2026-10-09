package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.entities.Authority;

import java.util.List;

public interface AuthorityService {

    Authority insertar(Authority authority);

    List<Authority> listarTodo();

    List<Authority> listarPorUsuario(Long userId);
}