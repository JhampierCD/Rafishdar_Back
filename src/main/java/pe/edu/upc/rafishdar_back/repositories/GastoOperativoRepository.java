package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.GastoOperativo;

import java.time.LocalDateTime;

public interface GastoOperativoRepository extends JpaRepository<GastoOperativo, Long> {
    GastoOperativo findByBitacoraId(Long idBitacora);

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
}
