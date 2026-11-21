package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.TemporadaDTO;
import pe.edu.upc.rafishdar_back.entities.Temporada;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.exceptions.ConflictException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.repositories.TemporadaRepository;
import pe.edu.upc.rafishdar_back.services.TemporadaService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TemporadaServiceImpl implements TemporadaService {

    @Autowired
    TemporadaRepository temporadaRepository;

    @Override
    public List<TemporadaDTO> listarTodo() {

        List<Temporada> temporadas =
                temporadaRepository.findAll();

        return convertirLista(temporadas);
    }


    @Override
    public TemporadaDTO buscarPorId(Long id) {
        if (id == null) {
            throw new BadRequestException("El ID de temporada es obligatorio");
        }

        Temporada temporada =
                temporadaRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Temporada no encontrada"));

        return convertirDTO(temporada);
    }


    @Override
    public TemporadaDTO insertar(
            TemporadaDTO temporadaDTO) {

        if (!datosValidos(temporadaDTO)) {
            throw new BadRequestException("Los datos de la temporada son obligatorios y deben ser válidos");
        }

        if (haySuperposicion(
                temporadaDTO.getFechaInicio(),
                temporadaDTO.getFechaFin(),
                null
        )) {
            throw new ConflictException("El rango de fechas se superpone con otra temporada");
        }

        Temporada temporada = new Temporada();

        temporada.setNombreTemporada(
                temporadaDTO
                        .getNombreTemporada()
                        .trim()
        );

        temporada.setFechaInicio(
                temporadaDTO.getFechaInicio()
        );

        temporada.setFechaFin(
                temporadaDTO.getFechaFin()
        );

        Temporada nuevaTemporada =
                temporadaRepository.save(temporada);

        return convertirDTO(nuevaTemporada);
    }


    @Override
    public TemporadaDTO actualizar(
            TemporadaDTO temporadaDTO) {

        if (temporadaDTO == null ||
                temporadaDTO.getId() == null) {
            throw new BadRequestException("El ID y los datos de actualización de temporada son obligatorios");
        }

        if (!datosValidos(temporadaDTO)) {
            throw new BadRequestException("Los datos de la temporada son obligatorios y deben ser válidos");
        }

        Temporada temporada =
                temporadaRepository
                        .findById(temporadaDTO.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Temporada no encontrada"));

        if (haySuperposicion(
                temporadaDTO.getFechaInicio(),
                temporadaDTO.getFechaFin(),
                temporada.getId()
        )) {
            throw new ConflictException("El rango de fechas se superpone con otra temporada");
        }

        temporada.setNombreTemporada(
                temporadaDTO
                        .getNombreTemporada()
                        .trim()
        );

        temporada.setFechaInicio(
                temporadaDTO.getFechaInicio()
        );

        temporada.setFechaFin(
                temporadaDTO.getFechaFin()
        );

        Temporada temporadaActualizada =
                temporadaRepository.save(temporada);

        return convertirDTO(temporadaActualizada);
    }


    @Override
    public boolean eliminar(Long id) {
        if (id == null) {
            throw new BadRequestException("El ID de temporada es obligatorio");
        }

        Temporada temporada = temporadaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Temporada no encontrada"));
        if (temporada.getCuotasPesca() != null && !temporada.getCuotasPesca().isEmpty()) {
            throw new ConflictException("No se puede eliminar una temporada asociada a cuotas de pesca");
        }
        temporadaRepository.delete(temporada);
        return true;
    }


    @Override
    public List<TemporadaDTO> listarVigentes() {

        List<Temporada> temporadas =
                temporadaRepository
                        .findTemporadasVigentes();

        return convertirLista(temporadas);
    }

    @Override
    public List<TemporadaDTO> buscarSuperpuestas(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null || inicio.isAfter(fin)) {
            throw new BadRequestException("El rango de fechas debe ser válido");
        }

        return convertirLista(
                temporadaRepository.findTemporadasSuperpuestas(inicio, fin)
        );
    }


    private boolean datosValidos(
            TemporadaDTO temporadaDTO) {

        if (temporadaDTO == null) {
            return false;
        }

        if (temporadaDTO.getNombreTemporada() == null ||
                temporadaDTO
                        .getNombreTemporada()
                        .trim()
                        .isEmpty()) {
            return false;
        }

        if (temporadaDTO
                .getNombreTemporada()
                .trim()
                .length() > 100) {
            return false;
        }

        if (temporadaDTO.getFechaInicio() == null ||
                temporadaDTO.getFechaFin() == null) {
            return false;
        }

        if (temporadaDTO
                .getFechaInicio()
                .isAfter(temporadaDTO.getFechaFin())) {
            return false;
        }

        return true;
    }

    private boolean haySuperposicion(
            LocalDate inicio,
            LocalDate fin,
            Long temporadaExcluidaId) {
        return temporadaRepository.findTemporadasSuperpuestas(inicio, fin)
                .stream()
                .anyMatch(temporada ->
                        !temporada.getId().equals(temporadaExcluidaId)
                );
    }


    private TemporadaDTO convertirDTO(
            Temporada temporada) {

        TemporadaDTO dto = new TemporadaDTO();

        dto.setId(temporada.getId());

        dto.setNombreTemporada(
                temporada.getNombreTemporada()
        );

        dto.setFechaInicio(
                temporada.getFechaInicio()
        );

        dto.setFechaFin(
                temporada.getFechaFin()
        );

        return dto;
    }


    private List<TemporadaDTO> convertirLista(
            List<Temporada> temporadas) {

        List<TemporadaDTO> listaDTO =
                new ArrayList<>();

        for (Temporada temporada : temporadas) {
            listaDTO.add(convertirDTO(temporada));
        }

        return listaDTO;
    }


}
