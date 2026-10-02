package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.entities.GastoOperativo;

import java.time.LocalDateTime;

public interface GastoOperativoService {
    GastoOperativo insertarGasto(Long idBitacora, GastoOperativo request);
    GastoOperativo actualizarGasto(Long idGasto, Double nuevoMonto);
    GastoOperativo buscarGastoPorBitacora(Long idBitacora);
    Double obtenerTotalGastosPorRango(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);
    Double obtenerPromedioGastosPorRango(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);
}
