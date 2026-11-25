package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.temporada.TemporadaDTO;
import pe.edu.upc.rafishdar_back.dtos.temporada.TemporadaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.temporada.TemporadaUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.Temporada;

import java.time.LocalDate;
import java.util.List;

public interface TemporadaService {

    List<TemporadaDTO> listarTodo();

    Temporada buscarPorId(Long id);

    Temporada insertar(TemporadaRequestDTO request);

    Temporada actualizar(Long id, TemporadaUpdateDTO request);

    Boolean eliminar(Long id);

    List<TemporadaDTO> listarVigentes();

    List<TemporadaDTO> buscarSuperpuestas(LocalDate inicio, LocalDate fin);

}