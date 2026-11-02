package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.Bitacora;
import pe.edu.upc.rafishdar_back.entities.Faena;

import java.time.LocalDateTime;
import java.util.List;

public interface FaenaRepository extends JpaRepository<Faena, Long> {
    List<Faena> findByUsuarioId(Long userId);
    List<Faena> findByUsuarioIdAndEstado(Long userId, String estado);

    // Validar que una faena no choque con otra
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM Faena f " +
            "WHERE (f.usuario.id = :usuarioId OR f.embarcacion.id = :embarcacionId) " +
            "AND f.estado <> 'Cancelada' " +
            "AND f.fechaHoraSalida < :fechaHoraLlegada " +
            "AND (f.fechaHoraLlegada IS NULL OR f.fechaHoraLlegada > :fechaHoraSalida) ")
    boolean existeFaenaSolapada(
            Long usuarioId,
            Long embarcacionId,
            LocalDateTime fechaHoraSalida,
            LocalDateTime fechaHoraLlegada
    );

    // Calcular el tiempo promedio de faena (en minutos) en un rango de fechas.
    // Usa TIMESTAMPDIFF (específico de MySQL) o la función equivalente de tu BD.
    // Solo se calcula sobre faenas 'Finalizada' porque son las únicas con fecha de llegada.
    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, f.fechaHoraSalida, f.fechaHoraLlegada)) " +
            "FROM Faena f " +
            "WHERE f.usuario.id = :usuarioId " +
            "AND f.estado = 'Finalizada' " +
            "AND f.fechaHoraSalida BETWEEN :inicio AND :fin")
    Double calcularTiempoPromedioFaenaMinutosEnRangoFecha(@Param("usuarioId") Long usuarioId,
                                              @Param("inicio") LocalDateTime inicio,
                                              @Param("fin") LocalDateTime fin);

    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, f.fechaHoraSalida, f.fechaHoraLlegada)) " +
            "FROM Faena f " +
            "WHERE f.usuario.id = :usuarioId " +
            "AND f.estado = 'Finalizada' ")
    Double calcularTiempoPromedioFaenaMinutos(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("UPDATE Faena f SET f.estado = 'Cancelada'" +
            "WHERE f.estado = 'Planificada'" +
            "AND f.fechaHoraSalida < :fechaLimite")
    int cancelarFaenasExpiradas(@Param("fechaLimite") LocalDateTime fechaLimite);
}
