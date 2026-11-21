package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.Faena;

import java.time.LocalDateTime;
import java.util.List;

public interface FaenaRepository extends JpaRepository<Faena, Long> {

    List<Faena> findByUsuarioId(Long idUsuario);

    List<Faena> findByUsuarioIdAndEstado(Long idUsuario, String estado);

    // Verifica si existe otra faena que se cruce con el intervalo solicitado.
    // Se excluye la propia faena cuando se está actualizando.
    @Query("""
            SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
            FROM Faena f
            WHERE (f.usuario.id = :usuarioId OR f.embarcacion.id = :embarcacionId)
            AND f.estado <> 'Cancelada'
            AND (:idFaenaExcluir IS NULL OR f.id <> :idFaenaExcluir)
            AND (
                COALESCE(f.fechaHoraSalida, f.fechaHoraSalidaProgramada) < :fechaHoraLlegada
            )
            AND (
                COALESCE(f.fechaHoraLlegada, f.fechaHoraLlegadaEstimada) IS NULL
                OR COALESCE(f.fechaHoraLlegada, f.fechaHoraLlegadaEstimada) > :fechaHoraSalida
            )
            """)
    boolean existeFaenaSolapada(
            @Param("usuarioId") Long usuarioId,
            @Param("embarcacionId") Long embarcacionId,
            @Param("fechaHoraSalida") LocalDateTime fechaHoraSalida,
            @Param("fechaHoraLlegada") LocalDateTime fechaHoraLlegada,
            @Param("idFaenaExcluir") Long idFaenaExcluir
    );

    // Cancela únicamente faenas planificadas cuya salida programada ya expiró.
    @Modifying
    @Query("""
            UPDATE Faena f
            SET f.estado = 'Cancelada'
            WHERE f.estado = 'Planificada'
            AND f.fechaHoraSalidaProgramada < :fechaLimite
            """)
    int cancelarFaenasExpiradas(@Param("fechaLimite") LocalDateTime fechaLimite);

    // Tiempo promedio real de las faenas finalizadas, expresado en minutos.
    @Query(value = """
            SELECT CAST(
                AVG(EXTRACT(EPOCH FROM (fecha_hora_llegada - fecha_hora_salida)) / 60.0)
                AS double precision
            )
            FROM faenas
            WHERE usuario_id = :usuarioId
            AND estado = 'Finalizada'
            AND fecha_hora_salida IS NOT NULL
            AND fecha_hora_llegada IS NOT NULL
            """, nativeQuery = true)
    Double calcularTiempoPromedioFaenaMinutos(
            @Param("usuarioId") Long usuarioId
    );

    // Tiempo promedio real de las faenas finalizadas dentro de un rango.
    @Query(value = """
            SELECT CAST(
                AVG(EXTRACT(EPOCH FROM (fecha_hora_llegada - fecha_hora_salida)) / 60.0)
                AS double precision
            )
            FROM faenas
            WHERE usuario_id = :usuarioId
            AND estado = 'Finalizada'
            AND fecha_hora_salida IS NOT NULL
            AND fecha_hora_llegada IS NOT NULL
            AND fecha_hora_salida BETWEEN :inicio AND :fin
            """, nativeQuery = true)
    Double calcularTiempoPromedioFaenaMinutosEnRangoFecha(
            @Param("usuarioId") Long usuarioId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
}