package pe.edu.upc.rafishdar_back.serviceimpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.EspeciePezDTO;
import pe.edu.upc.rafishdar_back.entities.EspeciePez;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.exceptions.ConflictException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
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
        if (id == null) {
            throw new BadRequestException("El ID de especie es obligatorio");
        }

        EspeciePez especie =
                especiePezRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Especie no encontrada"));

        return convertirDTO(especie);
    }


    @Override
    public EspeciePezDTO insertar(
            EspeciePezDTO especiePezDTO) {

        if (!datosValidos(especiePezDTO)) {
            throw new BadRequestException("Los datos de la especie son obligatorios y deben ser válidos");
        }
        validarNombreUnico(especiePezDTO, null);

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
            throw new BadRequestException("El ID y los datos de actualización de especie son obligatorios");
        }

        if (!datosValidos(especiePezDTO)) {
            throw new BadRequestException("Los datos de la especie son obligatorios y deben ser válidos");
        }
        validarNombreUnico(especiePezDTO, especiePezDTO.getId());

        EspeciePez especie =
                especiePezRepository
                        .findById(especiePezDTO.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Especie no encontrada"));

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

        if (id == null) {
            throw new BadRequestException("El ID de especie es obligatorio");
        }

        EspeciePez especie = especiePezRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especie no encontrada"));
        if ((especie.getCuotasPesca() != null && !especie.getCuotasPesca().isEmpty()) ||
                (especie.getDetallesCaptura() != null && !especie.getDetallesCaptura().isEmpty())) {
            throw new ConflictException("No se puede eliminar una especie asociada a cuotas o capturas");
        }
        especiePezRepository.delete(especie);
        return true;
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

    private void validarNombreUnico(EspeciePezDTO dto, Long idExcluir) {
        String nombreCientifico = dto.getNombreCientifico() == null
                ? null : dto.getNombreCientifico().trim();
        String nombreComun = dto.getNombreComun().trim();
        boolean nombreComunDuplicado = idExcluir == null
                ? especiePezRepository.existsByNombreComunIgnoreCase(nombreComun)
                : especiePezRepository.existsByNombreComunIgnoreCaseAndIdNot(nombreComun, idExcluir);
        boolean nombreCientificoDuplicado = nombreCientifico != null && !nombreCientifico.isBlank() &&
                (idExcluir == null
                        ? especiePezRepository.existsByNombreCientificoIgnoreCase(nombreCientifico)
                        : especiePezRepository.existsByNombreCientificoIgnoreCaseAndIdNot(nombreCientifico, idExcluir));
        if (nombreComunDuplicado || nombreCientificoDuplicado) {
            throw new ConflictException("Ya existe una especie con ese nombre común o científico");
        }
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
