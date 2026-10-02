package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.BitacoraFaena;

import java.time.LocalDateTime;
import java.util.List;

public interface BitacoraFaenaRepository extends JpaRepository<BitacoraFaena, Long> {
    List<BitacoraFaena> findByUserId(Long userId);
    List<BitacoraFaena> findByUserIdAndEstado(Long userId, String estado);

    @Query("SELECT b FROM BitacoraFaena b " +
            "WHERE b.zonaPesca.id = :idZona " +
            "AND b.fechaHoraSalida >= :desde " +
            "AND (LOWER(CAST(b.observaciones AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(CAST(b.estado AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))) ")
    List<BitacoraFaena> detectarIncidenciasRecientesEnZona(@Param("idZona") Long idZona,
                                                          @Param("desde") LocalDateTime desde,
                                                          @Param("keyword") String keyword);

    // Calcular el tiempo promedio de faena (en minutos) en un rango de fechas.
    // Usa TIMESTAMPDIFF (específico de MySQL) o la función equivalente de tu BD.
    // Solo se calcula sobre faenas 'Finalizada' porque son las únicas con fecha de llegada.
    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, b.fechaHoraSalida, b.fechaHoraLlegada)) " +
            "FROM BitacoraFaena b " +
            "WHERE b.usuario.id = :usuarioId " +
            "AND b.estado = 'Finalizada' " +
            "AND b.fechaHoraSalida BETWEEN :inicio AND :fin")
    Double calcularTiempoPromedioFaenaMinutos(@Param("usuarioId") Long usuarioId,
                                              @Param("inicio") LocalDateTime inicio,
                                              @Param("fin") LocalDateTime fin);

    // Busca bitácoras recientes en una zona donde los pescadores hayan reportado problemas comunes en texto libre.
    @Query("SELECT b FROM BitacoraFaena b " +
            "WHERE b.zona.id = :zonaId " +
            "AND b.fechaHoraLlegada >= :fechaLimite " +
            "AND (LOWER(b.observaciones) LIKE '%lobo%' " +
            "OR LOWER(b.observaciones) LIKE '%red rota%' " +
            "OR LOWER(b.observaciones) LIKE '%peligro%')")
    List<BitacoraFaena> detectarIncidenciasRecientesEnZona(@Param("zonaId") Long zonaId,
                                                           @Param("fechaLimite") LocalDateTime fechaLimite);
}
