package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.temporada.TemporadaDTO;
import pe.edu.upc.rafishdar_back.dtos.temporada.TemporadaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.temporada.TemporadaUpdateDTO;
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

        List<Temporada> temporadas = temporadaRepository.findAll();

        return convertirLista(temporadas);
    }


    @Override
    public Temporada buscarPorId(Long id) {
        if (id == null) {
            throw new BadRequestException("El ID de temporada es obligatorio");
        }

        return temporadaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Temporada no encontrada"));
    }


    @Override
    public Temporada insertar(TemporadaRequestDTO request) {
        if (!datosValidos(request)) {
            throw new BadRequestException("Los datos de la temporada son obligatorios y deben ser válidos");
        }
        if (haySuperposicion(request.getFechaInicio(), request.getFechaFin(), null)) {
            throw new ConflictException("El rango de fechas se superpone con otra temporada");
        }

        Temporada temporada = new Temporada();
        temporada.setNombreTemporada(request.getNombreTemporada().trim());
        temporada.setFechaInicio(request.getFechaInicio());
        temporada.setFechaFin(request.getFechaFin());
        return temporadaRepository.save(temporada);
    }


    @Override
    public Temporada actualizar(Long id, TemporadaUpdateDTO request) {
        if (request == null) {
            throw new BadRequestException("Los datos de actualización de temporada son obligatorios");
        }
        Temporada temporada = buscarPorId(id);
        if (request.getNombreTemporada() == null &&
                request.getFechaInicio() == null &&
                request.getFechaFin() == null) {
            throw new BadRequestException("Debe proporcionar al menos un dato para actualizar");
        }

        if (request.getNombreTemporada() != null) {
            if (request.getNombreTemporada().isBlank() ||
                    request.getNombreTemporada().trim().length() > 100) {
                throw new BadRequestException("El nombre de la temporada es obligatorio y debe tener máximo 100 caracteres");
            }
            temporada.setNombreTemporada(request.getNombreTemporada().trim());
        }

        LocalDate inicio = request.getFechaInicio() != null ? request.getFechaInicio() : temporada.getFechaInicio();
        LocalDate fin = request.getFechaFin() != null ? request.getFechaFin() : temporada.getFechaFin();
        if (inicio.isAfter(fin)) {
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        if (haySuperposicion(inicio, fin, temporada.getId())) {
            throw new ConflictException("El rango de fechas se superpone con otra temporada");
        }
        temporada.setFechaInicio(inicio);
        temporada.setFechaFin(fin);
        return temporadaRepository.save(temporada);
    }


    @Override
    public Boolean eliminar(Long id) {
        Temporada temporada = buscarPorId(id);
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


    private boolean datosValidos(TemporadaRequestDTO request) {
        if (request == null) {
            return false;
        }
        if (request.getNombreTemporada() == null ||
                request.getNombreTemporada().trim().isEmpty()) {
            return false;
        }
        if (request.getNombreTemporada().trim().length() > 100) {
            return false;
        }
        if (request.getFechaInicio() == null ||
                request.getFechaFin() == null) {
            return false;
        }
        if (request.getFechaInicio().isAfter(request.getFechaFin())) {
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