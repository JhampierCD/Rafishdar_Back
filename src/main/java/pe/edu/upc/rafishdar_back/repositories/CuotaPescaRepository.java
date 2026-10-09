package pe.edu.upc.rafishdar_back.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.rafishdar_back.entities.CuotaPesca;

import java.util.List;
import java.util.Optional;

public interface CuotaPescaRepository extends JpaRepository<CuotaPesca, Long> {

    CuotaPesca findByEspecie_IdAndTemporada_Id(
            Long especieId,
            Long temporadaId
    );

    @Query("SELECT c FROM CuotaPesca c " +
            "WHERE c.especie.id = :especieId AND c.temporada.id = :temporadaId")
    Optional<CuotaPesca> findByEspecieIdAndTemporadaId(
            @Param("especieId") Long especieId,
            @Param("temporadaId") Long temporadaId
    );

    List<CuotaPesca> findByEspecie_Id(Long especieId);

    List<CuotaPesca> findByTemporada_Id(Long temporadaId);

    @Query("SELECT c FROM CuotaPesca c JOIN FETCH c.especie " +
            "WHERE c.temporada.id = :temporadaId")
    List<CuotaPesca> findByTemporadaIdWithEspecie(
            @Param("temporadaId") Long temporadaId
    );
}
