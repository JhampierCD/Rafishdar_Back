package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.entities.Embarcacion;

import java.util.List;

public interface EmbarcacionService {
   public List<Embarcacion> listarEmbarcaciones();
   public Embarcacion buscarPorId(Long id);
   public Embarcacion insertar(Embarcacion embarcacion);
   public Embarcacion actualizar(Embarcacion embarcacion);
   public Boolean eliminar(Long id);
   List<Embarcacion> listarPorUsuario(Long usuarioId);
   boolean existeMatricula(String matricula);
}
