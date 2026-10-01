package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.entities.DetalleCaptura;

import java.util.List;

public interface DetalleCapturaService {
    DetalleCaptura insertarCaptura(Long idBitacora, DetalleCaptura request);
    DetalleCaptura actualizarCaptura(Long idDetalle, Double nuevoVolumen);
    void eliminarCaptura(Long idDetalle);
    List<DetalleCaptura> listarCapturasPorBitacora(Long idBitacora);
}
