package pe.edu.upc.rafishdar_back.serviceimpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.CuotaPescaDTO;
import pe.edu.upc.rafishdar_back.entities.CuotaPesca;
import pe.edu.upc.rafishdar_back.entities.EspeciePez;
import pe.edu.upc.rafishdar_back.entities.Temporada;
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

        List<CuotaPesca> cuotas =
                cuotaPescaRepository.findAll();

        return convertirLista(cuotas);
    }


    @Override
    public CuotaPescaDTO buscarPorId(Long id) {

        CuotaPesca cuota =
                cuotaPescaRepository
                        .findById(id)
                        .orElse(null);

        if (cuota == null) {
            return null;
        }

        return convertirDTO(cuota);
    }


    @Override
    public CuotaPescaDTO insertar(
            CuotaPescaDTO cuotaPescaDTO) {

        if (!datosValidos(cuotaPescaDTO)) {
            return null;
        }

        EspeciePez especie =
                especiePezRepository
                        .findById(
                                cuotaPescaDTO.getEspecieId()
                        )
                        .orElse(null);

        if (especie == null) {
            return null;
        }

        Temporada temporada =
                temporadaRepository
                        .findById(
                                cuotaPescaDTO.getTemporadaId()
                        )
                        .orElse(null);

        if (temporada == null) {
            return null;
        }

        CuotaPesca cuotaExistente =
                cuotaPescaRepository
                        .findByEspecie_IdAndTemporada_Id(
                                cuotaPescaDTO.getEspecieId(),
                                cuotaPescaDTO.getTemporadaId()
                        );

        if (cuotaExistente != null) {
            return null;
        }

        CuotaPesca cuota = new CuotaPesca();

        cuota.setLimiteToneladasIndustrial(
                cuotaPescaDTO
                        .getLimiteToneladasIndustrial()
        );

        cuota.setEspecie(especie);
        cuota.setTemporada(temporada);

        CuotaPesca nuevaCuota =
                cuotaPescaRepository.save(cuota);

        return convertirDTO(nuevaCuota);
    }


    @Override
    public CuotaPescaDTO actualizar(
            CuotaPescaDTO cuotaPescaDTO) {

        if (cuotaPescaDTO == null ||
                cuotaPescaDTO.getId() == null) {
            return null;
        }

        if (!datosValidos(cuotaPescaDTO)) {
            return null;
        }

        CuotaPesca cuota =
                cuotaPescaRepository
                        .findById(cuotaPescaDTO.getId())
                        .orElse(null);

        if (cuota == null) {
            return null;
        }

        EspeciePez especie =
                especiePezRepository
                        .findById(
                                cuotaPescaDTO.getEspecieId()
                        )
                        .orElse(null);

        if (especie == null) {
            return null;
        }

        Temporada temporada =
                temporadaRepository
                        .findById(
                                cuotaPescaDTO.getTemporadaId()
                        )
                        .orElse(null);

        if (temporada == null) {
            return null;
        }

        CuotaPesca cuotaRepetida =
                cuotaPescaRepository
                        .findByEspecie_IdAndTemporada_Id(
                                cuotaPescaDTO.getEspecieId(),
                                cuotaPescaDTO.getTemporadaId()
                        );

        if (cuotaRepetida != null &&
                !cuotaRepetida
                        .getId()
                        .equals(cuotaPescaDTO.getId())) {

            return null;
        }

        cuota.setLimiteToneladasIndustrial(
                cuotaPescaDTO
                        .getLimiteToneladasIndustrial()
        );

        cuota.setEspecie(especie);
        cuota.setTemporada(temporada);

        CuotaPesca cuotaActualizada =
                cuotaPescaRepository.save(cuota);

        return convertirDTO(cuotaActualizada);
    }


    @Override
    public boolean eliminar(Long id) {

        if (id == null ||
                !cuotaPescaRepository.existsById(id)) {
            return false;
        }

        try {

            cuotaPescaRepository.deleteById(id);

            return true;

        } catch (Exception e) {

            return false;
        }
    }


    @Override
    public CuotaPescaDTO buscarPorEspecieYTemporada(
            Long especieId,
            Long temporadaId) {

        if (especieId == null ||
                temporadaId == null) {
            return null;
        }

        CuotaPesca cuota =
                cuotaPescaRepository
                        .findByEspecie_IdAndTemporada_Id(
                                especieId,
                                temporadaId
                        );

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


    private boolean datosValidos(
            CuotaPescaDTO cuotaPescaDTO) {

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

        if (cuotaPescaDTO
                .getLimiteToneladasIndustrial() < 0) {
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
