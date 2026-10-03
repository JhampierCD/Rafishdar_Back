package pe.edu.upc.rafishdar_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.rafishdar_back.entities.BitacoraFaena;

import java.util.List;

public interface BitacoraFaenaRepository extends JpaRepository<BitacoraFaena, Long> {
    List<BitacoraFaena> findByUsuarioIdAndEstado(Long usuarioId, String estado);
    List<BitacoraFaena> findByZonaPescaIdAndEstadoAndFechaHoraSalidaAfter(Long zonaId, String estado, java.time.LocalDateTime desde);
    // US-47: radar de incidencias
}
