package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.DetalleCapturaRequestDTO;
import pe.edu.upc.rafishdar_back.entities.DetalleCaptura;

import java.time.LocalDateTime;
import java.util.List;

public interface DetalleCapturaService {
    List<DetalleCaptura> listarTodo();
    DetalleCaptura insertarCaptura(Long idBitacora, DetalleCapturaRequestDTO request);
    DetalleCaptura actualizarCaptura(Long idDetalle, Double nuevoVolumen);
    void eliminarCaptura(Long idDetalle);
    List<DetalleCaptura> listarCapturasPorBitacora(Long idBitacora);
    Double obtenerVolumenTotalPorRango(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);
    Double obtenerVolumenTotalPorEspecieYRango(Long idUsuario, Long idEspecie, LocalDateTime inicio, LocalDateTime fin);
    List<Long> obtenerTopZonasParaEspecieEnMesCorriente(Long idEspecie);
    Double obtenerPorcentajeCapturaIncidental(Long idBitacora);
}
