package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.entities.ZonaPesca;

import java.util.List;

public interface ZonaPescaService {
    public List<ZonaPesca> listarZonaPesca();
    public ZonaPesca buscarPorId(Long id);
    public ZonaPesca insertar(ZonaPesca zonaPesca);
    public ZonaPesca actualizar(ZonaPesca zonaPesca);
    public Boolean eliminar(Long id);
}
