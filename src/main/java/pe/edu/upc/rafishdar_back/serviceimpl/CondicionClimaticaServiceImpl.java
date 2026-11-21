package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.CondicionClimaticaDTO;
import pe.edu.upc.rafishdar_back.entities.Bitacora;
import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
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
    public CondicionClimatica insertarCondicionesPorBitacora(Long idBitacora, CondicionClimaticaDTO dto) {
        if (idBitacora == null || dto == null) {
            throw new BadRequestException("El ID de bitácora y los datos climáticos son obligatorios");
        }
        validarDatosClimaticos(dto.getTemperaturaCelsius(), dto.getVelocidadVientoNudos(), dto.getEstadoOleaje());

        Bitacora bitacora = bitacoraRepository.findById(idBitacora)
                .orElseThrow(() -> new ResourceNotFoundException("Bitácora no encontrada"));
        autorizacionFaena.validarAcceso(bitacora.getFaena(), true);

        CondicionClimatica condicion = new CondicionClimatica();
        condicion.setTemperaturaCelsius(dto.getTemperaturaCelsius());
        condicion.setVelocidadVientoNudos(dto.getVelocidadVientoNudos());
        condicion.setEstadoOleaje(dto.getEstadoOleaje());
        condicion.setBitacora(bitacora);

        return guardarOActualizar(condicion);
    }

    @Override
    public CondicionClimatica actualizarCondiciones(CondicionClimatica condicionClimatica) {
        if (condicionClimatica == null || condicionClimatica.getId() == null) {
            throw new BadRequestException("El ID y los datos de actualización son obligatorios");
        }
        CondicionClimatica foundCondicion = buscarPorId(condicionClimatica.getId());
        if (foundCondicion == null) {
            throw new ResourceNotFoundException("Condición climática no encontrada");
        }
        autorizacionFaena.validarAcceso(foundCondicion.getBitacora().getFaena(), true);

        validarDatosClimaticos(condicionClimatica.getTemperaturaCelsius(),
                condicionClimatica.getVelocidadVientoNudos(), condicionClimatica.getEstadoOleaje());
        if (condicionClimatica.getTemperaturaCelsius() == null &&
                condicionClimatica.getVelocidadVientoNudos() == null &&
                condicionClimatica.getEstadoOleaje() == null) {
            throw new BadRequestException("Debe enviar al menos un campo para actualizar");
        }

        if (condicionClimatica.getTemperaturaCelsius() != null)
            foundCondicion.setTemperaturaCelsius(condicionClimatica.getTemperaturaCelsius());
        if (condicionClimatica.getVelocidadVientoNudos() != null)
            foundCondicion.setVelocidadVientoNudos(condicionClimatica.getVelocidadVientoNudos());
        if (condicionClimatica.getEstadoOleaje() != null)
            foundCondicion.setEstadoOleaje(condicionClimatica.getEstadoOleaje());
        // BN-10: la referencia a bitácora no se puede cambiar en un update
        return condicionClimaticaRepository.save(foundCondicion);
    }

    @Override
    public CondicionClimatica buscarPorId(Long id) {
        if (id == null) {
            throw new BadRequestException("El ID de la condición climática es obligatorio");
        }
        return condicionClimaticaRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean eliminarCondiciones(Long id) {
        CondicionClimatica encontrada = buscarPorId(id);
        if (encontrada == null) return false;
        autorizacionFaena.validarAcceso(encontrada.getBitacora().getFaena(), true);

        condicionClimaticaRepository.delete(encontrada);
        return true;
    }

    @Override
    public CondicionClimatica guardarOActualizar(CondicionClimatica condicion) {
        if (condicion == null || condicion.getBitacora() == null || condicion.getBitacora().getId() == null) {
            throw new BadRequestException("La condición climática debe asociarse a una bitácora válida");
        }
        boolean bitacoraExiste = bitacoraRepository
                .existsById(condicion.getBitacora().getId());
        if (!bitacoraExiste) {
            throw new ResourceNotFoundException("Bitácora no encontrada");
        }
        Bitacora bitacora = bitacoraRepository.findById(condicion.getBitacora().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bitácora no encontrada"));
        autorizacionFaena.validarAcceso(bitacora.getFaena(), true);
        validarDatosClimaticos(condicion.getTemperaturaCelsius(),
                condicion.getVelocidadVientoNudos(), condicion.getEstadoOleaje());
        condicion.setBitacora(bitacora);

        // BN-10: si ya existe condición para esa bitácora, actualizar en lugar de crear
        Optional<CondicionClimatica> existente = condicionClimaticaRepository.findByBitacoraId(condicion.getBitacora().getId());

        if (existente.isPresent()) {
            CondicionClimatica actual = existente.get();
            if (condicion.getTemperaturaCelsius() != null)
                actual.setTemperaturaCelsius(condicion.getTemperaturaCelsius());
            if (condicion.getVelocidadVientoNudos() != null)
                actual.setVelocidadVientoNudos(condicion.getVelocidadVientoNudos());
            if (condicion.getEstadoOleaje() != null)
                actual.setEstadoOleaje(condicion.getEstadoOleaje());
            return condicionClimaticaRepository.save(actual);
        }

        return condicionClimaticaRepository.save(condicion);
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
