package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.Embarcacion;

import java.util.List;

public interface EmbarcacionRepository extends JpaRepository<Embarcacion, Long> {
    Embarcacion findEmbarcacionByMatriculaAndNombre(String matricula, String nombre);

    @Query("SELECT e FROM Embarcacion e WHERE e.usuario.id = :usuarioId")
    List<Embarcacion> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM Embarcacion e WHERE e.matricula = :matricula")
    boolean existsByMatricula(@Param("matricula") String matricula);
}
