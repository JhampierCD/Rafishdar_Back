package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.rafishdar_back.entities.Temporada;

import java.time.LocalDate;
import java.util.List;

public interface TemporadaRepository extends JpaRepository<Temporada, Long> {

    List<Temporada>
    findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

}
