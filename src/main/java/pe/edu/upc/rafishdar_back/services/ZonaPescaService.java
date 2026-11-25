package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.entities.ZonaPesca;
import pe.edu.upc.rafishdar_back.dtos.zona_pesca.ZonaPescaDTO;
import pe.edu.upc.rafishdar_back.dtos.zona_pesca.ZonaPescaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.zona_pesca.ZonaPescaUpdateDTO;

import java.util.List;

public interface ZonaPescaService {
    public List<ZonaPesca> listarZonaPesca();
    public List<ZonaPescaDTO> listarZonaPescaDTO();
    public ZonaPesca buscarPorId(Long id);
    public ZonaPesca insertar(ZonaPescaRequestDTO request);
    public ZonaPesca actualizar(Long id, ZonaPescaUpdateDTO request);
    public Boolean eliminar(Long id);
    List<ZonaPesca> listarTop3Historicas(Long especieId, int mes);
    List<ZonaPesca> listarCercanasACosta(Double distanciaMaxima);
}