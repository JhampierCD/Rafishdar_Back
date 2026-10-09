package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.TemporadaDTO;

import java.time.LocalDate;
import java.util.List;

public interface TemporadaService {

    List<TemporadaDTO> listarTodo();

    TemporadaDTO buscarPorId(Long id);

    TemporadaDTO insertar(TemporadaDTO temporadaDTO);

    TemporadaDTO actualizar(TemporadaDTO temporadaDTO);

    boolean eliminar(Long id);

    List<TemporadaDTO> listarVigentes();

    List<TemporadaDTO> buscarSuperpuestas(LocalDate inicio, LocalDate fin);

}
