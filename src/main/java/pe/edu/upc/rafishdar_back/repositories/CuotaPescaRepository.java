package pe.edu.upc.rafishdar_back.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.rafishdar_back.entities.CuotaPesca;

import java.util.List;

public interface CuotaPescaRepository extends JpaRepository<CuotaPesca, Long> {

    CuotaPesca findByEspecie_IdAndTemporada_Id(
            Long especieId,
            Long temporadaId
    );

    List<CuotaPesca> findByEspecie_Id(Long especieId);

    List<CuotaPesca> findByTemporada_Id(Long temporadaId);

}
