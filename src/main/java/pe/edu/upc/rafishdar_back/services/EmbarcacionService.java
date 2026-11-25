package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.entities.Embarcacion;
import pe.edu.upc.rafishdar_back.dtos.embarcacion.EmbarcacionRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.embarcacion.EmbarcacionUpdateDTO;
import pe.edu.upc.rafishdar_back.dtos.embarcacion.EmbarcacionDTO;

import java.util.List;

public interface EmbarcacionService {
   public List<Embarcacion> listarEmbarcaciones();
   public List<EmbarcacionDTO> listarEmbarcacionesDTO();
   public Embarcacion buscarPorId(Long id);
   public Embarcacion insertar(EmbarcacionRequestDTO request);
   public Embarcacion actualizar(Long id, EmbarcacionUpdateDTO request);
   public Boolean eliminar(Long id);
   List<Embarcacion> listarPorUsuario(Long usuarioId);
   boolean existeMatricula(String matricula);
}