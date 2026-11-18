package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraDTO;
import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.Bitacora;
import pe.edu.upc.rafishdar_back.entities.Faena;

import java.time.LocalDateTime;
import java.util.List;

public interface BitacoraService {
    List<Bitacora> listarTodo();
    List<BitacoraDTO> listarTodoDTO();
    Bitacora buscarBitacoraPorId(Long id);
    Bitacora insertarBitacora(BitacoraRequestDTO bitacora);
    Bitacora actualizarBitacora(BitacoraUpdateDTO bitacora);
    void eliminarBitacora(Long id);
    Boolean validarFaenaEnCurso(Faena faena);
    Boolean verificarDesvioDeZona(Long bitacoraId);
    Boolean hayAlertaDeIncidenciasEnZona(Long idZona, String keyword);
}
