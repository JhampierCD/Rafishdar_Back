package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.Temporada;

import java.time.LocalDate;
import java.util.List;

public interface TemporadaRepository extends JpaRepository<Temporada, Long> {

    List<Temporada>
    findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    @Query("SELECT t FROM Temporada t " +
            "WHERE t.fechaInicio <= CURRENT_DATE AND t.fechaFin >= CURRENT_DATE")
    List<Temporada> findTemporadasVigentes();

    @Query("SELECT t FROM Temporada t " +
            "WHERE t.fechaInicio <= :fin AND t.fechaFin >= :inicio")
    List<Temporada> findTemporadasSuperpuestas(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );
}
