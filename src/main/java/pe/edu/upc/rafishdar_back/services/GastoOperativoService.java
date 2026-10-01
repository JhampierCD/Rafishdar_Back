package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.entities.GastoOperativo;

public interface GastoOperativoService {
    GastoOperativo insertarGasto(Long idBitacora, GastoOperativo request);
    GastoOperativo actualizarGasto(Long idGasto, Double nuevoMonto);
    GastoOperativo buscarGastoPorBitacora(Long idBitacora);
}
