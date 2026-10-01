package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.rafishdar_back.entities.BitacoraFaena;
import pe.edu.upc.rafishdar_back.entities.DetalleCaptura;

import java.util.List;

public interface DetalleCapturaRepository extends JpaRepository<DetalleCaptura, Long> {
    List<DetalleCaptura> findByBitacoraId(Long idBitacora);
}
