package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.ZonaPesca;

import java.util.List;

public interface ZonaPescaRepository extends JpaRepository<ZonaPesca, Long> {
    ZonaPesca findZonaPescaByNombreZona(String nombreZona);

    @Query(value = "SELECT z.* FROM zonas_pesca z " +
            "JOIN bitacoras_faena b ON z.id = b.zona_pesca_id " +
            "JOIN detalles_captura d ON b.id = d.bitacora_id " +
            "WHERE d.especie_id = :especieId " +
            "AND EXTRACT(MONTH FROM b.fecha_hora_llegada) = :mes " +
            "GROUP BY z.id " +
            "ORDER BY SUM(d.volumen_kg) DESC " +
            "LIMIT 3", nativeQuery = true)
    List<ZonaPesca> findTop3ZonasByEspecieAndMesHistorico(
            @Param("especieId") Long especieId,
            @Param("mes") int mes
    );

    @Query("SELECT z FROM ZonaPesca z " +
            "WHERE z.distanciaCostaKm <= :distanciaMaxima " +
            "ORDER BY z.distanciaCostaKm ASC")
    List<ZonaPesca> findZonasCercanasACosta(
            @Param("distanciaMaxima") Double distanciaMaxima
    );
}
