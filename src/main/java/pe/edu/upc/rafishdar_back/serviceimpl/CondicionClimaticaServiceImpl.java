package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.condicion_climatica.CondicionClimaticaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.condicion_climatica.CondicionClimaticaUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.Bitacora;
import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.exceptions.ConflictException;
import pe.edu.upc.rafishdar_back.repositories.BitacoraRepository;
import pe.edu.upc.rafishdar_back.repositories.CondicionClimaticaRepository;
import pe.edu.upc.rafishdar_back.services.CondicionClimaticaService;

import java.util.List;
import java.util.Optional;

@Service
public class CondicionClimaticaServiceImpl implements CondicionClimaticaService {
    @Autowired
    private CondicionClimaticaRepository condicionClimaticaRepository;

    @Autowired
    private BitacoraRepository bitacoraRepository;

    @Autowired
    private FaenaAuthorizationService autorizacionFaena;

    @Override
    public List<CondicionClimatica> listarTodoCondiciones() {
        return condicionClimaticaRepository.findAll();
    }

    @Override
    public CondicionClimatica insertarCondicionesPorBitacora(Long idBitacora, CondicionClimaticaRequestDTO request) {
        validarIdBitacora(idBitacora);
        if (request == null) {
            throw new BadRequestException("Los datos climáticos son obligatorios");
        }
        validarDatosClimaticos(request.getTemperaturaCelsius(),
                request.getVelocidadVientoNudos(), request.getEstadoOleaje());
        if (request.getTemperaturaCelsius() == null &&
                request.getVelocidadVientoNudos() == null &&
                request.getEstadoOleaje() == null) {
            throw new BadRequestException("Debe enviar al menos un dato climático");
        }

        Bitacora bitacora = bitacoraRepository.findById(idBitacora)
                .orElseThrow(() -> new ResourceNotFoundException("Bitácora no encontrada"));
        autorizacionFaena.validarAcceso(bitacora.getFaena(), true);

        CondicionClimatica condicion = new CondicionClimatica();
        condicion.setTemperaturaCelsius(request.getTemperaturaCelsius());
        condicion.setVelocidadVientoNudos(request.getVelocidadVientoNudos());
        condicion.setEstadoOleaje(request.getEstadoOleaje());
        condicion.setBitacora(bitacora);
        if (condicionClimaticaRepository.existsByBitacoraId(idBitacora)) {
            throw new ConflictException("La bitácora ya tiene una condición climática registrada");
        }
        return condicionClimaticaRepository.save(condicion);
    }

    @Override
    public CondicionClimatica actualizarCondiciones(Long id, CondicionClimaticaUpdateDTO request) {
        if (request == null) {
            throw new BadRequestException("Los datos de actualización son obligatorios");
        }
        CondicionClimatica foundCondicion = buscarPorId(id);
        autorizacionFaena.validarAcceso(foundCondicion.getBitacora().getFaena(), true);

        validarDatosClimaticos(request.getTemperaturaCelsius(),
                request.getVelocidadVientoNudos(), request.getEstadoOleaje());
        if (request.getTemperaturaCelsius() == null &&
                request.getVelocidadVientoNudos() == null &&
                request.getEstadoOleaje() == null) {
            throw new BadRequestException("Debe enviar al menos un campo para actualizar");
        }

        if (request.getTemperaturaCelsius() != null)
            foundCondicion.setTemperaturaCelsius(request.getTemperaturaCelsius());
        if (request.getVelocidadVientoNudos() != null)
            foundCondicion.setVelocidadVientoNudos(request.getVelocidadVientoNudos());
        if (request.getEstadoOleaje() != null)
            foundCondicion.setEstadoOleaje(request.getEstadoOleaje());
        // BN-10: la referencia a bitácora no se puede cambiar en un update
        return condicionClimaticaRepository.save(foundCondicion);
    }

    @Override
    public CondicionClimatica buscarPorId(Long id) {
        if (id == null) {
            throw new BadRequestException("El ID de la condición climática es obligatorio");
        }
        return condicionClimaticaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Condición climática no encontrada"));
    }

    @Override
    public Boolean eliminarCondiciones(Long id) {
        CondicionClimatica encontrada = buscarPorId(id);
        autorizacionFaena.validarAcceso(encontrada.getBitacora().getFaena(), true);

        condicionClimaticaRepository.delete(encontrada);
        return true;
    }

    @Override
    public Optional<CondicionClimatica> consultarPorBitacoraConPermisoUsuario(Long bitacoraId, Long usuarioId) {
        validarIdBitacora(bitacoraId);
        if (usuarioId == null) {
            throw new BadRequestException("El ID del usuario es obligatorio");
        }
        autorizacionFaena.validarAccesoUsuario(usuarioId);
        return condicionClimaticaRepository.findByBitacoraIdAndUsuarioId(bitacoraId, usuarioId);
    }

    @Override
    public Optional<CondicionClimatica> consultarPorBitacora(Long bitacoraId) {
        validarIdBitacora(bitacoraId);
        Bitacora bitacora = bitacoraRepository.findById(bitacoraId)
                .orElseThrow(() -> new ResourceNotFoundException("Bitácora no encontrada"));
        autorizacionFaena.validarAcceso(bitacora.getFaena(), false);
        return condicionClimaticaRepository.findByBitacoraId(bitacoraId);
    }

    @Override
    public boolean existeCondicionParaBitacora(Long bitacoraId) {
        return condicionClimaticaRepository.existsByBitacoraId(bitacoraId);
    }

    @Override
    public Double obtenerPromedioTemperaturaPorZona(Long zonaId) {
        if (zonaId == null) {
            throw new BadRequestException("El ID de zona es obligatorio");
        }
        return condicionClimaticaRepository.promedioTemperaturaPorZona(zonaId);
    }

    @Override
    public List<CondicionClimatica> listarCondicionesPeligrosas(Double umbralVientoNudos) {
        if (umbralVientoNudos == null || !Double.isFinite(umbralVientoNudos) || umbralVientoNudos <= 0) {
            throw new BadRequestException("El umbral de viento debe ser positivo y finito");
        }
        return condicionClimaticaRepository
                .findByVelocidadVientoNudosGreaterThanEqual(umbralVientoNudos);
    }

    private void validarDatosClimaticos(Double temperatura, Double viento, String oleaje) {
        if (temperatura != null && !Double.isFinite(temperatura)) {
            throw new BadRequestException("La temperatura debe ser finita");
        }
        if (viento != null && (!Double.isFinite(viento) || viento < 0)) {
            throw new BadRequestException("La velocidad del viento debe ser finita y no negativa");
        }
        if (oleaje != null && oleaje.isBlank()) {
            throw new BadRequestException("El estado del oleaje no puede estar vacío");
        }
    }

    private void validarIdBitacora(Long bitacoraId) {
        if (bitacoraId == null) {
            throw new BadRequestException("El ID de bitácora es obligatorio");
        }
    }
}