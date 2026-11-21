package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.GastoOperativo;

import java.time.LocalDateTime;
import java.util.Optional;

public interface GastoOperativoRepository extends JpaRepository<GastoOperativo, Long> {
    Optional<GastoOperativo> findByFaenaId(Long faenaId);
    void deleteByFaenaId(Long faenaId);

    // Calcular la suma total de gastos (combustible + insumos) en un rango de fechas
    @Query("SELECT SUM(g.costoCombustible + g.costoHieloInsumos) " +
            "FROM GastoOperativo g " +
            "WHERE g.faena.usuario.id = :usuarioId " +
            "AND g.faena.fechaHoraSalida BETWEEN :inicio AND :fin")
    Double calcularTotalGastosOperativos(@Param("usuarioId") Long usuarioId,
                                         @Param("inicio") LocalDateTime inicio,
                                         @Param("fin") LocalDateTime fin);

    // Calcular el promedio de gasto por faena en un rango de fechas
    @Query("SELECT AVG(g.costoCombustible + g.costoHieloInsumos) " +
            "FROM GastoOperativo g " +
            "WHERE g.faena.usuario.id = :usuarioId " +
            "AND g.faena.fechaHoraSalida BETWEEN :inicio AND :fin")
    Double calcularPromedioGastosPorFaena(@Param("usuarioId") Long usuarioId,
                                          @Param("inicio") LocalDateTime inicio,
                                          @Param("fin") LocalDateTime fin);

    // En BitacoraFaenaRepository (aprovechando los JOINs implícitos de JPA)
// Calcula cuántos Kg de pescado se obtienen por cada galón de combustible invertido en una zona específica.
    @Query(value = """
            SELECT
                (SELECT COALESCE(SUM(d.volumen_kg), 0)
                 FROM detalles_captura d
                 JOIN bitacoras b ON b.id = d.bitacora_id
                 JOIN faenas f ON f.id = b.faena_id
                 WHERE b.zona_pesca_id = :zonaId
                   AND f.estado = 'Finalizada'
                   AND f.fecha_hora_salida >= :fechaDesde)
                /
                NULLIF((SELECT COALESCE(SUM(g.galones_combustible), 0)
                        FROM gastos_operativos g
                        JOIN faenas f ON f.id = g.faena_id
                        WHERE f.estado = 'Finalizada'
                          AND f.fecha_hora_salida >= :fechaDesde
                          AND EXISTS (SELECT 1 FROM bitacoras b
                                      WHERE b.faena_id = f.id
                                        AND b.zona_pesca_id = :zonaId)), 0)
            """, nativeQuery = true)
    Double calcularIndiceKgPorGalonEnZona(@Param("zonaId") Long zonaId,
                                          @Param("fechaDesde") LocalDateTime fechaDesde);
}
