package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.gasto_operativo.GastoOperativoRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.gasto_operativo.GastoOperativoUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.GastoOperativo;

import java.time.LocalDateTime;
import java.util.List;

public interface GastoOperativoService {
    List<GastoOperativo> listarTodo();
    GastoOperativo insertarGasto(Long idFaena, GastoOperativoRequestDTO request);
    GastoOperativo actualizarGasto(Long idFaena, GastoOperativoUpdateDTO request);
    GastoOperativo buscarGastoPorFaena(Long idFaena);
    Boolean eliminar(Long idFaena);
    Double obtenerTotalGastosPorRango(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);
    Double obtenerPromedioGastosPorRango(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);
    Double obtenerEficienciaCombustiblePorZona(Long idZona, int diasAtras);
}