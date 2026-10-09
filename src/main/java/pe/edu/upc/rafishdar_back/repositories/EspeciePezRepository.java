package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.EspeciePez;

import java.util.List;

public interface EspeciePezRepository extends JpaRepository<EspeciePez, Long> {

    @Query("SELECT e FROM EspeciePez e WHERE e.estadoVeda = :estadoVeda")
    List<EspeciePez> findByEstadoVeda(@Param("estadoVeda") Boolean estadoVeda);

    @Query("SELECT e FROM EspeciePez e " +
            "WHERE LOWER(e.nombreComun) LIKE LOWER(CONCAT('%', :nombreComun, '%'))")
    List<EspeciePez> buscarPorNombreComun(@Param("nombreComun") String nombreComun);
}
