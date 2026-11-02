package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.faena.FaenaDTO;
import pe.edu.upc.rafishdar_back.dtos.faena.FaenaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.faena.FaenaUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.Faena;

import java.time.LocalDateTime;
import java.util.List;

public interface FaenaService {
    List<Faena> listarTodo();
    List<FaenaDTO> listarTodoDTO();
    List<FaenaDTO> listarFaenasPorUsuarioId(Long usuarioId);
    Faena buscarFaenaPorId(Long id);
    Faena insertarFaena(FaenaRequestDTO request);
    Faena cambiarEstadoAPlanificada(Long idFaena);
    Faena cambiarEstadoAEnCurso(Long idFaena);
    Faena actualizarPlanificada(Long idFaena, FaenaUpdateDTO datosNuevos);
    Faena actualizarEnCurso(Long idFaena, FaenaUpdateDTO datosNuevos);
    Faena terminarFaena(Long idFaena, Boolean ultimaBitacora, BitacoraRequestDTO request);
    Faena cancelarFaenaEnCursoOFinalizada(Long idFaena);
    void eliminarFaenaPlanificada(Long idFaena);
    List<FaenaDTO> listarFaenasPorUsuarioYEstado(Long usuarioId, String estado);
    Double obtenerTiempoPromedioFaena(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);
    void automatizarCancelacionFaenasExpiradas();
}
