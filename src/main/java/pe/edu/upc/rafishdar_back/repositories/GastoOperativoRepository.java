package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.rafishdar_back.entities.GastoOperativo;

public interface GastoOperativoRepository extends JpaRepository<GastoOperativo, Long> {
    GastoOperativo findByBitacoraId(Long idBitacora);
}
