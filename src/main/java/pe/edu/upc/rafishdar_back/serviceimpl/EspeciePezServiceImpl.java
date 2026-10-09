package pe.edu.upc.rafishdar_back.serviceimpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.EspeciePezDTO;
import pe.edu.upc.rafishdar_back.entities.EspeciePez;
import pe.edu.upc.rafishdar_back.repositories.EspeciePezRepository;
import pe.edu.upc.rafishdar_back.services.EspeciePezService;

import java.util.ArrayList;
import java.util.List;

@Service
public class EspeciePezServiceImpl implements EspeciePezService {

    @Autowired
    EspeciePezRepository especiePezRepository;

    @Override
    public List<EspeciePezDTO> listarTodo() {

        List<EspeciePez> especies =
                especiePezRepository.findAll();

        return convertirLista(especies);
    }


    @Override
    public EspeciePezDTO buscarPorId(Long id) {

        EspeciePez especie =
                especiePezRepository
                        .findById(id)
                        .orElse(null);

        if (especie == null) {
            return null;
        }

        return convertirDTO(especie);
    }


    @Override
    public EspeciePezDTO insertar(
            EspeciePezDTO especiePezDTO) {

        if (!datosValidos(especiePezDTO)) {
            return null;
        }

        EspeciePez especie = new EspeciePez();

        especie.setNombreComun(
                especiePezDTO.getNombreComun().trim()
        );

        if (especiePezDTO.getNombreCientifico() == null ||
                especiePezDTO.getNombreCientifico()
                        .trim()
                        .isEmpty()) {

            especie.setNombreCientifico(null);

        } else {

            especie.setNombreCientifico(
                    especiePezDTO
                            .getNombreCientifico()
                            .trim()
            );
        }

        especie.setEstadoVeda(
                especiePezDTO.getEstadoVeda()
        );

        EspeciePez nuevaEspecie =
                especiePezRepository.save(especie);

        return convertirDTO(nuevaEspecie);
    }


    @Override
    public EspeciePezDTO actualizar(
            EspeciePezDTO especiePezDTO) {

        if (especiePezDTO == null ||
                especiePezDTO.getId() == null) {
            return null;
        }

        if (!datosValidos(especiePezDTO)) {
            return null;
        }

        EspeciePez especie =
                especiePezRepository
                        .findById(especiePezDTO.getId())
                        .orElse(null);

        if (especie == null) {
            return null;
        }

        especie.setNombreComun(
                especiePezDTO.getNombreComun().trim()
        );

        if (especiePezDTO.getNombreCientifico() == null ||
                especiePezDTO.getNombreCientifico()
                        .trim()
                        .isEmpty()) {

            especie.setNombreCientifico(null);

        } else {

            especie.setNombreCientifico(
                    especiePezDTO
                            .getNombreCientifico()
                            .trim()
            );
        }

        especie.setEstadoVeda(
                especiePezDTO.getEstadoVeda()
        );

        EspeciePez especieActualizada =
                especiePezRepository.save(especie);

        return convertirDTO(especieActualizada);
    }


    @Override
    public boolean eliminar(Long id) {

        if (id == null ||
                !especiePezRepository.existsById(id)) {
            return false;
        }

        try {

            especiePezRepository.deleteById(id);

            return true;

        } catch (Exception e) {

            return false;
        }
    }


    @Override
    public List<EspeciePezDTO> listarPorEstadoVeda(
            Boolean estadoVeda) {

        if (estadoVeda == null) {
            return new ArrayList<>();
        }

        List<EspeciePez> especies =
                especiePezRepository
                        .findByEstadoVeda(estadoVeda);

        return convertirLista(especies);
    }

    @Override
    public List<EspeciePezDTO> buscarPorNombreComun(String nombreComun) {
        if (nombreComun == null || nombreComun.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return convertirLista(
                especiePezRepository.buscarPorNombreComun(nombreComun.trim())
        );
    }


    private boolean datosValidos(
            EspeciePezDTO especiePezDTO) {

        if (especiePezDTO == null) {
            return false;
        }

        if (especiePezDTO.getNombreComun() == null ||
                especiePezDTO.getNombreComun()
                        .trim()
                        .isEmpty()) {
            return false;
        }

        if (especiePezDTO
                .getNombreComun()
                .trim()
                .length() > 100) {
            return false;
        }

        if (especiePezDTO.getNombreCientifico() != null &&
                especiePezDTO
                        .getNombreCientifico()
                        .trim()
                        .length() > 150) {
            return false;
        }

        if (especiePezDTO.getEstadoVeda() == null) {
            return false;
        }

        return true;
    }


    private EspeciePezDTO convertirDTO(
            EspeciePez especie) {

        EspeciePezDTO dto = new EspeciePezDTO();

        dto.setId(especie.getId());
        dto.setNombreComun(especie.getNombreComun());
        dto.setNombreCientifico(
                especie.getNombreCientifico()
        );
        dto.setEstadoVeda(especie.getEstadoVeda());

        return dto;
    }


    private List<EspeciePezDTO> convertirLista(
            List<EspeciePez> especies) {

        List<EspeciePezDTO> listaDTO =
                new ArrayList<>();

        for (EspeciePez especie : especies) {
            listaDTO.add(convertirDTO(especie));
        }

        return listaDTO;
    }

}
