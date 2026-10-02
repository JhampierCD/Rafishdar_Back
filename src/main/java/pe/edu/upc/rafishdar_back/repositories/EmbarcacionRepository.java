package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.rafishdar_back.entities.Embarcacion;

public interface EmbarcacionRepository extends JpaRepository<Embarcacion, Long> {
}
