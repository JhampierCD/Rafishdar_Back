package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    User findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);

    @Query("SELECT u FROM User u " +
            "WHERE LOWER(u.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) " +
            "OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<User> buscarPorNombreOApellido(@Param("termino") String termino);
}
