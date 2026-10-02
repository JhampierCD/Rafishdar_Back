package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.BitacoraFaena;

import java.time.LocalDateTime;
import java.util.List;

public interface BitacoraFaenaRepository extends JpaRepository<BitacoraFaena, Long> {
    List<BitacoraFaena> findByUsuarioId(Long usuarioId);
    List<BitacoraFaena> findByUsuarioIdAndEstado(Long usuarioId, String estado);

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
}
