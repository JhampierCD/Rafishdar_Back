package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.Authority;

import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findByName(String name);

    @Query("SELECT a FROM Authority a JOIN a.users u WHERE u.id = :userId")
    List<Authority> findByUserId(@Param("userId") Long userId);
}