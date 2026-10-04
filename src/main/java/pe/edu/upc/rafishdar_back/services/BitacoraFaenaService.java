package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.entities.BitacoraFaena;

import java.time.LocalDateTime;
import java.util.List;

public interface BitacoraFaenaService {
    BitacoraFaena insertarBitacora(BitacoraFaena bitacoraFaena);
    BitacoraFaena buscarBitacoraPorId(Long id);
    List<BitacoraFaena> listarBitacorasPorUsuarioId(Long usuarioId);
    BitacoraFaena cambiarEstadoAPlanificada(Long idBitacora);
    BitacoraFaena cambiarEstadoAEnCurso(Long idBitacora);
    BitacoraFaena actualizarPlanificada(Long idBitacora, BitacoraFaena datosNuevos);
    BitacoraFaena actualizarEnCurso(Long idBitacora, BitacoraFaena datosNuevos);
    BitacoraFaena terminarBitacora(Long idBitacora, String observaciones);
    void eliminarBitacoraPlanificada(Long idBitacora);
    List<BitacoraFaena> listarBitacorasPorUsuarioYEstado(Long usuarioId, String estado);
    Double obtenerTiempoPromedioFaena(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);
    Boolean hayAlertaDeIncidenciasEnZona(Long idZona);
    void automatizarCancelacionFaenasExpiradas();
    boolean verificarDesvioDeZona(Long idBitacora);
}
