package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.DetalleCaptura;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface DetalleCapturaRepository extends JpaRepository<DetalleCaptura, Long> {
    List<DetalleCaptura> findByBitacoraId(Long idBitacora);

    // Calcular el volumen total capturado (todas las especies) en un rango de fechas
    @Query("SELECT SUM(d.volumenKg) " +
            "FROM DetalleCaptura d JOIN d.bitacora b " +
            "WHERE b.usuario.id = :usuarioId " +
            "AND b.fechaHoraSalida BETWEEN :inicio AND :fin")
    Double calcularVolumenTotal(@Param("usuarioId") Long usuarioId,
                                @Param("inicio") LocalDateTime inicio,
                                @Param("fin") LocalDateTime fin);

    // Calcular el volumen total capturado filtrado por una ESPECIE específica
    @Query("SELECT SUM(d.volumenKg) " +
            "FROM DetalleCaptura d JOIN d.bitacora b " +
            "WHERE b.usuario.id = :usuarioId " +
            "AND d.especie.id = :especieId " +
            "AND b.fechaHoraSalida BETWEEN :inicio AND :fin")
    Double calcularVolumenPorEspecie(@Param("usuarioId") Long usuarioId,
                                     @Param("especieId") Long especieId,
                                     @Param("inicio") LocalDateTime inicio,
                                     @Param("fin") LocalDateTime fin);

    // Retorna una lista de zonas y su volumen total, ordenada de mayor a menor captura para una especie en un mes específico.
// Se usa Pageable (ej. PageRequest.of(0, 3)) para obtener solo el "Top 3" de zonas.
    @Query("SELECT b.zonaPesca.id, SUM(d.volumenKg) AS totalKg " +
            "FROM DetalleCaptura d JOIN d.bitacora b " +
            "WHERE d.especie.id = :especieId " +
            "AND MONTH(b.fechaHoraSalida) = :mes " +
            "GROUP BY b.zonaPesca.id " +
            "ORDER BY totalKg DESC")
    List<Object[]> encontrarMejoresZonasPorEspecieYMes(@Param("especieId") Long especieId,
                                                       @Param("mes") int mes,
                                                       PageRequest pageable);

    // Calcula la suma de kilogramos de especies que están en veda para una bitácora específica.
    @Query("SELECT SUM(d.volumenKg) FROM DetalleCaptura d " +
            "WHERE d.bitacora.id = :bitacoraId " +
            "AND d.especie.estadoVeda = true")
    Double calcularVolumenCapturaEnVeda(@Param("bitacoraId") Long bitacoraId);
}
