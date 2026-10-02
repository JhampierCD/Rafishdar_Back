package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.DetalleCaptura;

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
}
