package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.BitacoraFaenaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.BitacoraFaenaUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.BitacoraFaena;

import java.time.LocalDateTime;
import java.util.List;

public interface BitacoraFaenaService {
    BitacoraFaena insertarBitacora(BitacoraFaenaRequestDTO bitacoraFaena);
    BitacoraFaena buscarBitacoraPorId(Long id);
    List<BitacoraFaena> listarBitacorasPorUsuarioId(Long usuarioId);
    BitacoraFaena cambiarEstadoAEnCurso(Long idBitacora);
    BitacoraFaena cambiarCoordenadas(Long idBitacora, Double latitud, Double longitud);
    BitacoraFaena terminarBitacora(Long idBitacora, String observaciones);
    BitacoraFaena cambiarEstadoAPlanificada(Long idBitacora, BitacoraFaenaUpdateDTO request);
    void eliminarBitacoraPlanificada(Long idBitacora);
    List<BitacoraFaena> listarBitacorasPorUsuarioYEstado(Long usuarioId, String estado);
    Double obtenerTiempoPromedioFaena(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);
    Boolean hayAlertaDeIncidenciasEnZona(Long idZona);
}
