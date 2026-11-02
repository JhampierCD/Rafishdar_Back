package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.detalle_captura.DetalleCapturaDTO;
import pe.edu.upc.rafishdar_back.dtos.detalle_captura.DetalleCapturaRequestDTO;
import pe.edu.upc.rafishdar_back.entities.DetalleCaptura;
import pe.edu.upc.rafishdar_back.entities.Temporada;

import java.time.LocalDateTime;
import java.util.List;

public interface DetalleCapturaService {
    List<DetalleCaptura> listarTodo();
    List<DetalleCapturaDTO> listarTodoDTO();
    List<DetalleCapturaDTO> listarCapturasPorBitacora(Long idBitacora);
    DetalleCaptura insertarCaptura(Long idBitacora, DetalleCapturaRequestDTO request);
    DetalleCaptura actualizarCaptura(Long idDetalle, Double nuevoVolumen);
    void eliminarCaptura(Long idDetalle);
    Boolean validarVolumenMenorACuota(Long idUsuario, Long idEspecie, Double volumenActual, Temporada temporada);
    Double obtenerVolumenTotalPorRango(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);
    Double obtenerVolumenTotalPorEspecieYRango(Long idUsuario, Long idEspecie, LocalDateTime inicio, LocalDateTime fin);
    List<Long> obtenerTopZonasParaEspecieEnMesCorriente(Long idEspecie);
    Double obtenerPorcentajeCapturaIncidental(Long idBitacora);
}
