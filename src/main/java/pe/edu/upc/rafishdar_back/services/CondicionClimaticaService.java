package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.CondicionClimaticaDTO;
import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;

import java.util.List;

public interface CondicionClimaticaService {
    List<CondicionClimatica> listarTodoCondiciones();
    CondicionClimatica insertarCondiciones(CondicionClimatica condicionClimatica);
    CondicionClimatica actualizarCondiciones(CondicionClimatica condicionClimatica);
    CondicionClimatica buscarPorId(Long id);
    Boolean eliminarCondiciones(Long id);

}
