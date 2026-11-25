package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.cuota_pesca.CuotaPescaDTO;
import pe.edu.upc.rafishdar_back.dtos.cuota_pesca.CuotaPescaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.cuota_pesca.CuotaPescaUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.CuotaPesca;

import java.util.List;

public interface CuotaPescaService {

    List<CuotaPescaDTO> listarTodo();
    CuotaPesca buscarPorId(Long id);
    CuotaPescaDTO buscarPorIdDTO(Long id);
    CuotaPesca insertarCuota(CuotaPescaRequestDTO cuotaPescaDTO);
    CuotaPesca actualizarCuota(Long idCuota, CuotaPescaUpdateDTO request);
    Boolean eliminarCuota(Long id);
    CuotaPescaDTO buscarPorEspecieYTemporada(
            Long especieId,
            Long temporadaId
    );
    List<CuotaPescaDTO> listarPorEspecie(Long especieId);
    List<CuotaPescaDTO> listarPorTemporada(Long temporadaId);
    List<CuotaPescaDTO> listarPorTemporadaConEspecie(Long temporadaId);
}