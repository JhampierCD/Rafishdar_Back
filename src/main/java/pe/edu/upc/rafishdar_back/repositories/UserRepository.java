package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.rafishdar_back.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
