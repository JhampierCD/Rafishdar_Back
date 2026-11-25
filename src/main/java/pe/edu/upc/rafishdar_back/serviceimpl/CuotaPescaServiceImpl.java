package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.cuota_pesca.CuotaPescaDTO;
import pe.edu.upc.rafishdar_back.dtos.especie_pez.EspeciePezDTO;
import pe.edu.upc.rafishdar_back.dtos.cuota_pesca.CuotaPescaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.cuota_pesca.CuotaPescaUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.CuotaPesca;
import pe.edu.upc.rafishdar_back.entities.EspeciePez;
import pe.edu.upc.rafishdar_back.entities.Temporada;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.exceptions.ConflictException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.repositories.CuotaPescaRepository;
import pe.edu.upc.rafishdar_back.repositories.EspeciePezRepository;
import pe.edu.upc.rafishdar_back.repositories.TemporadaRepository;
import pe.edu.upc.rafishdar_back.services.CuotaPescaService;

import java.util.ArrayList;
import java.util.List;

@Service
public class CuotaPescaServiceImpl implements CuotaPescaService {

    @Autowired
    CuotaPescaRepository cuotaPescaRepository;

    @Autowired
    EspeciePezRepository especiePezRepository;

    @Autowired
    TemporadaRepository temporadaRepository;


    @Override
    public List<CuotaPescaDTO> listarTodo() {

        List<CuotaPesca> cuotas = cuotaPescaRepository.findAll();

        return convertirLista(cuotas);
    }

    @Override
    public CuotaPesca buscarPorId(Long id) {
        if (id == null) throw new BadRequestException("El ID de la cuota es obligatorio");
        return cuotaPescaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuota de pesca no encontrada"));
    }

    @Override
    public CuotaPescaDTO buscarPorIdDTO(Long id) {

        CuotaPesca cuota = cuotaPescaRepository.findById(id).orElse(null);

        if (cuota == null) {
            return null;
        }

        return convertirDTO(cuota);
    }


    @Override
    public CuotaPesca insertarCuota(CuotaPescaRequestDTO requestDTO) {

        if (!datosValidos(requestDTO)) {
            throw new BadRequestException("Los datos de la cuota de pesca son obligatorios y deben ser válidos");
        }

        EspeciePez especie = especiePezRepository.findById(requestDTO.getEspecieId())
                .orElseThrow(() -> new ResourceNotFoundException("Especie no encontrada"));

        Temporada temporada = temporadaRepository.findById(requestDTO.getTemporadaId())
                .orElseThrow(() -> new ResourceNotFoundException("Temporada no encontrada"));

        if (cuotaPescaRepository.findByEspecieIdAndTemporadaId(
                requestDTO.getEspecieId(), requestDTO.getTemporadaId()).isPresent()) {
            throw new ConflictException("Ya existe una cuota de pesca para esta especie y temporada");
        }

        CuotaPesca cuota = new CuotaPesca();

        cuota.setLimiteToneladasIndustrial(requestDTO.getLimiteToneladasIndustrial());
        cuota.setEspecie(especie);
        cuota.setTemporada(temporada);

        return cuotaPescaRepository.save(cuota);
    }


    @Override
    public CuotaPesca actualizarCuota(Long idCuota, CuotaPescaUpdateDTO request) {
        if (request == null) {
            throw new BadRequestException("Los datos de actualización de la cuota son obligatorios");
        }
        CuotaPesca foundCuota = buscarPorId(idCuota);

        Double limiteToneladasIndustrial = request.getLimiteToneladasIndustrial();
        if (limiteToneladasIndustrial == null || !Double.isFinite(limiteToneladasIndustrial) ||
                limiteToneladasIndustrial < 0) {
            throw new BadRequestException("El límite de toneladas industrial debe ser finito y no negativo");
        }

        foundCuota.setLimiteToneladasIndustrial(limiteToneladasIndustrial);

        return cuotaPescaRepository.save(foundCuota);
    }


    @Override
    public Boolean eliminarCuota(Long id) {
        if (id == null) {
            throw new BadRequestException("El ID de la cuota es obligatorio");
        }
        CuotaPesca cuota = cuotaPescaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuota de pesca no encontrada"));
        cuotaPescaRepository.delete(cuota);
        return true;
    }


    @Override
    public CuotaPescaDTO buscarPorEspecieYTemporada(Long especieId, Long temporadaId) {
        if (especieId == null || temporadaId == null) {
            return null;
        }

        CuotaPesca cuota = cuotaPescaRepository.findByEspecieIdAndTemporadaId(especieId, temporadaId)
                .orElse(null);

        if (cuota == null) {
            return null;
        }

        return convertirDTO(cuota);
    }


    @Override
    public List<CuotaPescaDTO> listarPorEspecie(
            Long especieId) {

        if (especieId == null) {
            return new ArrayList<>();
        }

        List<CuotaPesca> cuotas =
                cuotaPescaRepository
                        .findByEspecie_Id(especieId);

        return convertirLista(cuotas);
    }


    @Override
    public List<CuotaPescaDTO> listarPorTemporada(
            Long temporadaId) {

        if (temporadaId == null) {
            return new ArrayList<>();
        }

        List<CuotaPesca> cuotas =
                cuotaPescaRepository
                        .findByTemporada_Id(temporadaId);

        return convertirLista(cuotas);
    }

    @Override
    public List<CuotaPescaDTO> listarPorTemporadaConEspecie(Long temporadaId) {
        if (temporadaId == null) {
            return new ArrayList<>();
        }

        List<CuotaPesca> cuotas =
                cuotaPescaRepository.findByTemporadaIdWithEspecie(temporadaId);
        List<CuotaPescaDTO> cuotasDTO = convertirLista(cuotas);

        for (int i = 0; i < cuotas.size(); i++) {
            EspeciePez especie = cuotas.get(i).getEspecie();
            EspeciePezDTO especieDTO = new EspeciePezDTO();
            especieDTO.setId(especie.getId());
            especieDTO.setNombreComun(especie.getNombreComun());
            especieDTO.setNombreCientifico(especie.getNombreCientifico());
            especieDTO.setEstadoVeda(especie.getEstadoVeda());
            cuotasDTO.get(i).setEspecie(especieDTO);
        }

        return cuotasDTO;
    }

    private boolean datosValidos(
            CuotaPescaRequestDTO cuotaPescaDTO) {

        if (cuotaPescaDTO == null) {
            return false;
        }

        if (cuotaPescaDTO.getEspecieId() == null ||
                cuotaPescaDTO.getTemporadaId() == null) {
            return false;
        }

        if (cuotaPescaDTO
                .getLimiteToneladasIndustrial() == null) {
            return false;
        }

        if (!Double.isFinite(cuotaPescaDTO.getLimiteToneladasIndustrial()) ||
                cuotaPescaDTO.getLimiteToneladasIndustrial() < 0) {
            return false;
        }

        return true;
    }


    private CuotaPescaDTO convertirDTO(
            CuotaPesca cuota) {

        CuotaPescaDTO dto = new CuotaPescaDTO();

        dto.setId(cuota.getId());

        dto.setLimiteToneladasIndustrial(
                cuota.getLimiteToneladasIndustrial()
        );

        dto.setEspecieId(
                cuota.getEspecie().getId()
        );

        dto.setTemporadaId(
                cuota.getTemporada().getId()
        );

        return dto;
    }


    private List<CuotaPescaDTO> convertirLista(
            List<CuotaPesca> cuotas) {

        List<CuotaPescaDTO> listaDTO =
                new ArrayList<>();

        for (CuotaPesca cuota : cuotas) {
            listaDTO.add(convertirDTO(cuota));
        }

        return listaDTO;
    }

}