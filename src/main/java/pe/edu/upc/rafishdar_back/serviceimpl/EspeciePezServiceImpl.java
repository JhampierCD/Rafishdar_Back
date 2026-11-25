package pe.edu.upc.rafishdar_back.serviceimpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.especie_pez.EspeciePezDTO;
import pe.edu.upc.rafishdar_back.dtos.especie_pez.EspeciePezRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.especie_pez.EspeciePezUpdateDTO;
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
    public EspeciePez buscarPorId(Long id) {
        if (id == null) {
            throw new BadRequestException("El ID de especie es obligatorio");
        }

        return especiePezRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especie no encontrada"));
    }


    @Override
    public EspeciePez insertar(EspeciePezRequestDTO request) {

        if (!datosValidos(request)) {
            throw new BadRequestException("Los datos de la especie son obligatorios y deben ser válidos");
        }
        validarNombreUnico(request, null);

        EspeciePez especie = new EspeciePez();

        especie.setNombreComun(
                request.getNombreComun().trim()
        );

        if (request.getNombreCientifico() == null ||
                request.getNombreCientifico().trim().isEmpty()) {
            especie.setNombreCientifico(null);
        } else {
            especie.setNombreCientifico(request.getNombreCientifico().trim());
        }

        especie.setEstadoVeda(request.getEstadoVeda());
        return especiePezRepository.save(especie);
    }


    @Override
    public EspeciePez actualizar(Long id, EspeciePezUpdateDTO request) {
        if (request == null) {
            throw new BadRequestException("Los datos de actualización de especie son obligatorios");
        }

        EspeciePez especie = buscarPorId(id);
        if (request.getNombreComun() == null && request.getNombreCientifico() == null &&
                request.getEstadoVeda() == null) {
            throw new BadRequestException("Debe proporcionar al menos un dato para actualizar");
        }
        if (request.getNombreComun() != null) {
            if (request.getNombreComun().isBlank() || request.getNombreComun().trim().length() > 100) {
                throw new BadRequestException("El nombre común es obligatorio y debe tener máximo 100 caracteres");
            }
            especie.setNombreComun(request.getNombreComun().trim());
        }
        if (request.getNombreCientifico() != null) {
            if (request.getNombreCientifico().trim().length() > 150) {
                throw new BadRequestException("El nombre científico debe tener máximo 150 caracteres");
            }
            if (request.getNombreCientifico().isBlank()) {
                especie.setNombreCientifico(null);
            } else {
                especie.setNombreCientifico(request.getNombreCientifico().trim());
            }
        }
        if (request.getEstadoVeda() != null) {
            especie.setEstadoVeda(request.getEstadoVeda());
        }

        validarNombreUnico(especie.getNombreComun(), especie.getNombreCientifico(), id);
        return especiePezRepository.save(especie);
    }

    @Override
    public Boolean eliminar(Long id) {
        EspeciePez especie = buscarPorId(id);
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


    private boolean datosValidos(EspeciePezRequestDTO request) {
        if (request == null) {
            return false;
        }
        if (request.getNombreComun() == null ||
                request.getNombreComun().trim().isEmpty()) {
            return false;
        }
        if (request.getNombreComun().trim().length() > 100) {
            return false;
        }
        if (request.getNombreCientifico() != null &&
                request.getNombreCientifico().trim().length() > 150) {
            return false;
        }
        if (request.getEstadoVeda() == null) {
            return false;
        }
        return true;
    }

    private void validarNombreUnico(EspeciePezRequestDTO dto, Long idExcluir) {
        String nombreCientifico = dto.getNombreCientifico() == null
                ? null : dto.getNombreCientifico().trim();
        validarNombreUnico(dto.getNombreComun().trim(), nombreCientifico, idExcluir);
    }

    private void validarNombreUnico(String nombreComun, String nombreCientifico, Long idExcluir) {
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