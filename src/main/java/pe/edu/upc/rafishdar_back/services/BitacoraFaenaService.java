package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.entities.BitacoraFaena;

import java.util.List;

public interface BitacoraFaenaService {
    BitacoraFaena insertarBitacora(BitacoraFaena bitacoraFaena);
    BitacoraFaena buscarBitacoraPorId(Long id);
    List<BitacoraFaena> listarBitacorasPorUsuarioId(Long usuarioId);
    BitacoraFaena cambiarEstadoAEnCurso(Long idBitacora);
    BitacoraFaena cambiarCoordenadas(Long idBitacora, Double latitud, Double longitud);
    BitacoraFaena terminarBitacora(Long idBitacora, String observaciones);
    BitacoraFaena cambiarEstadoAPlanificada(Long idBitacora, BitacoraFaena request);
    void eliminarBitacoraPlanificada(Long idBitacora);
}
