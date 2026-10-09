package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.TemporadaDTO;
import pe.edu.upc.rafishdar_back.entities.Temporada;
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

        Temporada temporada =
                temporadaRepository
                        .findById(id)
                        .orElse(null);

        if (temporada == null) {
            return null;
        }

        return convertirDTO(temporada);
    }


    @Override
    public TemporadaDTO insertar(
            TemporadaDTO temporadaDTO) {

        if (!datosValidos(temporadaDTO)) {
            return null;
        }

        if (haySuperposicion(
                temporadaDTO.getFechaInicio(),
                temporadaDTO.getFechaFin(),
                null
        )) {
            return null;
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
            return null;
        }

        if (!datosValidos(temporadaDTO)) {
            return null;
        }

        Temporada temporada =
                temporadaRepository
                        .findById(temporadaDTO.getId())
                        .orElse(null);

        if (temporada == null) {
            return null;
        }

        if (haySuperposicion(
                temporadaDTO.getFechaInicio(),
                temporadaDTO.getFechaFin(),
                temporada.getId()
        )) {
            return null;
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

        if (id == null ||
                !temporadaRepository.existsById(id)) {
            return false;
        }

        try {

            temporadaRepository.deleteById(id);

            return true;

        } catch (Exception e) {

            return false;
        }
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
            return new ArrayList<>();
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
