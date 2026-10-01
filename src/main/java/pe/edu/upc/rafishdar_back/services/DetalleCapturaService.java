package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.entities.DetalleCaptura;

import java.util.List;

public interface DetalleCapturaService {
    DetalleCaptura addCaptura(Long idBitacora, DetalleCaptura request);
    DetalleCaptura updateCaptura(Long idDetalle, Double nuevoVolumen);
    void deleteCaptura(Long idDetalle);
    List<DetalleCaptura> getCapturasByBitacora(Long idBitacora);
}
