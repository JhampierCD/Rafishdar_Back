package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.GastoOperativo;

import java.time.LocalDateTime;
import java.util.Optional;

public interface GastoOperativoRepository extends JpaRepository<GastoOperativo, Long> {
    Optional<GastoOperativo> findByBitacoraId(Long idBitacora);
    void deleteByBitacoraId(Long idBitacora);

    // Calcular la suma total de gastos (combustible + insumos) en un rango de fechas
    @Query("SELECT SUM(g.costoCombustible + g.costoHieloInsumos) " +
            "FROM GastoOperativo g JOIN g.bitacora b " +
            "WHERE b.usuario.id = :usuarioId " +
            "AND b.fechaHoraSalida BETWEEN :inicio AND :fin")
    Double calcularTotalGastosOperativos(@Param("usuarioId") Long usuarioId,
                                         @Param("inicio") LocalDateTime inicio,
                                         @Param("fin") LocalDateTime fin);

    // Calcular el promedio de gasto por faena en un rango de fechas
    @Query("SELECT AVG(g.costoCombustible + g.costoHieloInsumos) " +
            "FROM GastoOperativo g JOIN g.bitacora b " +
            "WHERE b.usuario.id = :usuarioId " +
            "AND b.fechaHoraSalida BETWEEN :inicio AND :fin")
    Double calcularPromedioGastosPorFaena(@Param("usuarioId") Long usuarioId,
                                          @Param("inicio") LocalDateTime inicio,
                                          @Param("fin") LocalDateTime fin);

    // En BitacoraFaenaRepository (aprovechando los JOINs implícitos de JPA)
// Calcula cuántos Kg de pescado se obtienen por cada galón de combustible invertido en una zona específica.
    @Query("SELECT SUM(d.volumenKg) / SUM(g.galonesCombustible) " +
            "FROM BitacoraFaena b " +
            "JOIN DetalleCaptura d ON d.bitacora.id = b.id " +
            "JOIN GastoOperativo g ON g.bitacora.id = b.id " +
            "WHERE b.zonaPesca.id = :zonaId " +
            "AND b.estado = 'Finalizada' " +
            "AND b.fechaHoraSalida >= :fechaDesde")
    Double calcularIndiceKgPorGalonEnZona(@Param("zonaId") Long zonaId,
                                          @Param("fechaDesde") LocalDateTime fechaDesde);
}
