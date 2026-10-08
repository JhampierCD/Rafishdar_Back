package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.rafishdar_back.entities.EspeciePez;

import java.util.List;

public interface EspeciePezRepository extends JpaRepository<EspeciePez, Long> {

    List<EspeciePez> findByEstadoVeda(Boolean estadoVeda);

}
