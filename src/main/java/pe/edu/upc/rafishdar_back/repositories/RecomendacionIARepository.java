package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.RecomendacionIA;

import java.util.List;
import java.util.Optional;

public interface RecomendacionIARepository extends JpaRepository<RecomendacionIA, Long> {
    // BN-23: verificar si ya existe recomendación para la bitácora (1:1)
    boolean existsByBitacoraId(Long bitacoraId);

    // BN-23: recuperar la recomendación existente para hacer upsert en regeneración
    Optional<RecomendacionIA> findByBitacoraId(Long bitacoraId);

    // US-25: consulta validando propietario (el pescador solo ve las suyas)
    @Query("""
        SELECT r FROM RecomendacionIA r
        WHERE r.bitacora.id    = :bitacoraId
        AND   r.bitacora.usuario.id = :usuarioId
    """)
    Optional<RecomendacionIA> findByBitacoraIdAndUsuarioId(
            @Param("bitacoraId") Long bitacoraId,
            @Param("usuarioId") Long usuarioId
    );

    // BN-24: buscar por nivel de riesgo válido (BAJO / MEDIO / ALTO)
    List<RecomendacionIA> findByNivelRiesgo(String nivelRiesgo);

    // US-34 Dashboard: últimas N recomendaciones de un usuario,
    // ordenadas por fecha de generación descendente
    @Query("""
        SELECT r FROM RecomendacionIA r
        WHERE r.bitacora.usuario.id = :usuarioId
        ORDER BY r.fechaGeneracion DESC
    """)
    List<RecomendacionIA> findUltimasByUsuario(@Param("usuarioId") Long usuarioId);

    // US-33 Técnico: recomendaciones con nivel de riesgo ALTO en los últimos días
    @Query("""
        SELECT r FROM RecomendacionIA r
        WHERE r.nivelRiesgo = 'ALTO'
        AND r.fechaGeneracion >= :desde
        ORDER BY r.fechaGeneracion DESC
    """)
    List<RecomendacionIA> findRecomendacionesAltoRiesgoDesde(
            @Param("desde") java.time.LocalDateTime desde
    );

    // Conteo de recomendaciones por nivel de riesgo para dashboard (US-34)
    long countByNivelRiesgoAndBitacoraUsuarioId(String nivelRiesgo, Long usuarioId);
}
