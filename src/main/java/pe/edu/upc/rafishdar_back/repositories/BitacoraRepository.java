package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.Bitacora;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BitacoraRepository extends JpaRepository<Bitacora, Long> {
    Optional<Bitacora> findFirstByFaenaIdOrderByFechaHoraRegistroDesc(Long faenaId);

    // Busca bitácoras recientes en una zona donde los pescadores hayan reportado problemas comunes en texto libre.
//    @Query("SELECT b FROM Bitacora b " +
//            "WHERE b.zonaPesca.id = :zonaId " +
//            "AND b.fechaHoraLlegada >= :fechaLimite " +
//            "AND (LOWER(b.observaciones) LIKE '%lobo%' " +
//            "OR LOWER(b.observaciones) LIKE '%red rota%' " +
//            "OR LOWER(b.observaciones) LIKE '%peligro%')")
//    List<Bitacora> detectarIncidenciasRecientesEnZona(@Param("zonaId") Long zonaId,
//                                                      @Param("fechaLimite") LocalDateTime fechaLimite);

    // Busca bitácoras recientes en una zona donde los pescadores hayan reportado problemas comunes en texto libre.
    // keyword = "lobo", "red rota", "peligro"
    @Query("SELECT b FROM Bitacora b " +
            "WHERE b.zonaPesca.id = :idZona " +
            "AND b.fechaHoraSalida >= :fechaLimite " +
            "AND LOWER(CAST(b.observaciones AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) ")
    List<Bitacora> detectarIncidenciasRecientesEnZona(@Param("idZona") Long idZona,
                                                      @Param("fechaLimite") LocalDateTime fechaLimite,
                                                      @Param("keyword") String keyword);
}
