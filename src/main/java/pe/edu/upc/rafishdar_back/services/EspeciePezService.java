package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.EspeciePezDTO;

import java.util.List;

public interface EspeciePezService {

    List<EspeciePezDTO> listarTodo();

    EspeciePezDTO buscarPorId(Long id);

    EspeciePezDTO insertar(EspeciePezDTO especiePezDTO);

    EspeciePezDTO actualizar(EspeciePezDTO especiePezDTO);

    boolean eliminar(Long id);

    List<EspeciePezDTO> listarPorEstadoVeda(Boolean estadoVeda);

}
