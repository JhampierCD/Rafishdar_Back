package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;

import java.util.List;
import java.util.Optional;

public interface CondicionClimaticaRepository extends JpaRepository<CondicionClimatica, Long>{
    boolean existsByBitacoraId(Long bitacoraId);

    // BN-10: recuperar la condición existente para hacer upsert
    Optional<CondicionClimatica> findByBitacoraId(Long bitacoraId);

    // US-12: condición de bitácora propia (con validación de propietario en service)
    @Query("""
        SELECT c FROM CondicionClimatica c
        WHERE c.bitacora.id = :bitacoraId
        AND c.bitacora.usuario.id = :usuarioId
    """)
    Optional<CondicionClimatica> findByBitacoraIdAndUsuarioId(
            @Param("bitacoraId") Long bitacoraId,
            @Param("usuarioId") Long usuarioId
    );

    // US-11, BN-11: advertencia por viento peligroso (> umbral en nudos)
    List<CondicionClimatica> findByVelocidadVientoNudosGreaterThanEqual(Double umbralNudos);

    // US-05 / US-13 histórico: promedio de temperatura por zona
    // Usado en reportes y dashboard (US-34)
    @Query("""
        SELECT AVG(c.temperaturaCelsius)
        FROM CondicionClimatica c
        WHERE c.bitacora.zonaPesca.id = :zonaId
    """)
    Double promedioTemperaturaPorZona(@Param("zonaId") Long zonaId);

    // US-13 histórico: condiciones de una zona para el mes en curso
    // Apoya US-46 (mapa de calor histórico estacional)
    @Query("""
        SELECT c FROM CondicionClimatica c
        WHERE c.bitacora.zonaPesca.id = :zonaId
        AND FUNCTION('MONTH', c.bitacora.fechaHoraSalida) = :mes
        AND FUNCTION('YEAR',  c.bitacora.fechaHoraSalida) = :anio
    """)
    List<CondicionClimatica> findByZonaYMes(
            @Param("zonaId") Long zonaId,
            @Param("mes") int mes,
            @Param("anio") int anio
    );
}
