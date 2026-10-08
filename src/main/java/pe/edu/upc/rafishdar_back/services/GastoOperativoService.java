package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.GastoOperativoRequestDTO;
import pe.edu.upc.rafishdar_back.entities.GastoOperativo;

import java.time.LocalDateTime;

public interface GastoOperativoService {
    GastoOperativo insertarGasto(Long idBitacora, GastoOperativoRequestDTO request);
    GastoOperativo actualizarGasto(Long idGasto, GastoOperativoRequestDTO request);
    GastoOperativo buscarGastoPorBitacora(Long idBitacora);
    Double obtenerTotalGastosPorRango(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);
    Double obtenerPromedioGastosPorRango(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);
    Double obtenerEficienciaCombustiblePorZona(Long idZona, int diasAtras);
}
