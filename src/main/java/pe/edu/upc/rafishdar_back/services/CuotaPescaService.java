package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.cuota_pesca.CuotaPescaDTO;
import pe.edu.upc.rafishdar_back.dtos.cuota_pesca.CuotaPescaReqUpdDTO;
import pe.edu.upc.rafishdar_back.entities.CuotaPesca;

import java.util.List;

public interface CuotaPescaService {

    List<CuotaPescaDTO> listarTodo();
    CuotaPescaDTO buscarPorIdDTO(Long id);
    CuotaPesca insertar(CuotaPescaReqUpdDTO cuotaPescaDTO);
    CuotaPesca actualizar(CuotaPescaReqUpdDTO cuotaPescaDTO);
    boolean eliminar(Long id);
    CuotaPescaDTO buscarPorEspecieYTemporada(
            Long especieId,
            Long temporadaId
    );
    List<CuotaPescaDTO> listarPorEspecie(Long especieId);
    List<CuotaPescaDTO> listarPorTemporada(Long temporadaId);
    List<CuotaPescaDTO> listarPorTemporadaConEspecie(Long temporadaId);
}