package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.especie_pez.EspeciePezDTO;
import pe.edu.upc.rafishdar_back.dtos.especie_pez.EspeciePezRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.especie_pez.EspeciePezUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.EspeciePez;

import java.util.List;

public interface EspeciePezService {

    List<EspeciePezDTO> listarTodo();

    EspeciePez buscarPorId(Long id);

    EspeciePez insertar(EspeciePezRequestDTO request);

    EspeciePez actualizar(Long id, EspeciePezUpdateDTO request);

    Boolean eliminar(Long id);

    List<EspeciePezDTO> listarPorEstadoVeda(Boolean estadoVeda);

    List<EspeciePezDTO> buscarPorNombreComun(String nombreComun);

}