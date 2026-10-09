package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCase(String correo);

    @Query("SELECT u FROM User u " +
            "WHERE LOWER(u.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) " +
            "OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<User> buscarPorNombreOApellido(@Param("termino") String termino);
}
