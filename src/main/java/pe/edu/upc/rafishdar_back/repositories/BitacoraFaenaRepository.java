package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.rafishdar_back.entities.BitacoraFaena;

import java.util.List;

public interface BitacoraFaenaRepository extends JpaRepository<BitacoraFaena, Long> {
    List<BitacoraFaena> findByUsuarioId(Long usuarioId);
    List<BitacoraFaena> findByUsuarioIdAndEstado(Long usuarioId, String estado);
}
