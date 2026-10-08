package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.CuotaPescaDTO;

import java.util.List;

public interface CuotaPescaService {

    List<CuotaPescaDTO> listarTodo();

    CuotaPescaDTO buscarPorId(Long id);

    CuotaPescaDTO insertar(CuotaPescaDTO cuotaPescaDTO);

    CuotaPescaDTO actualizar(CuotaPescaDTO cuotaPescaDTO);

    boolean eliminar(Long id);

    CuotaPescaDTO buscarPorEspecieYTemporada(
            Long especieId,
            Long temporadaId
    );

    List<CuotaPescaDTO> listarPorEspecie(Long especieId);

    List<CuotaPescaDTO> listarPorTemporada(Long temporadaId);
}