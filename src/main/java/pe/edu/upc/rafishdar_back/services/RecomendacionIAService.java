package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.entities.RecomendacionIA;

import java.util.List;

public interface RecomendacionIAService {
    List<RecomendacionIA> listarTodoRecomendaciones();
    RecomendacionIA insertarRecomendaciones(RecomendacionIA recomendacionIA);
    RecomendacionIA actualizarRecomendaciones(RecomendacionIA recomendacionIA);
    RecomendacionIA buscarPorId(Long id);
    Boolean eliminarRecomendaciones(Long id);

}
