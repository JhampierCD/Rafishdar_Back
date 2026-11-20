package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;

import java.util.List;
import java.util.Optional;

public interface CondicionClimaticaRepository extends JpaRepository<CondicionClimatica, Long> {
    boolean existsByBitacoraId(Long bitacoraId);

    Optional<CondicionClimatica> findByBitacoraId(Long bitacoraId);

    @Query("""
        SELECT c FROM CondicionClimatica c
        WHERE c.bitacora.id = :bitacoraId
        AND c.bitacora.faena.usuario.id = :usuarioId
    """)
    Optional<CondicionClimatica> findByBitacoraIdAndUsuarioId(
            @Param("bitacoraId") Long bitacoraId,
            @Param("usuarioId") Long usuarioId
    );

    List<CondicionClimatica> findByVelocidadVientoNudosGreaterThanEqual(Double umbralNudos);

    @Query("""
        SELECT AVG(c.temperaturaCelsius)
        FROM CondicionClimatica c
        WHERE c.bitacora.zonaPesca.id = :zonaId
    """)
    Double promedioTemperaturaPorZona(@Param("zonaId") Long zonaId);

    @Query("""
        SELECT c FROM CondicionClimatica c
        WHERE c.bitacora.zonaPesca.id = :zonaId
        AND FUNCTION('MONTH', c.bitacora.fechaHoraRegistro) = :mes
        AND FUNCTION('YEAR',  c.bitacora.fechaHoraRegistro) = :anio
    """)
    List<CondicionClimatica> findByZonaYMes(
            @Param("zonaId") Long zonaId,
            @Param("mes") int mes,
            @Param("anio") int anio
    );
}