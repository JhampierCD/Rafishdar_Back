package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.CondicionClimaticaDTO;
import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;
import pe.edu.upc.rafishdar_back.repositories.BitacoraFaenaRepository;
import pe.edu.upc.rafishdar_back.repositories.CondicionClimaticaRepository;
import pe.edu.upc.rafishdar_back.services.CondicionClimaticaService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CondicionClimaticaServiceImpl implements CondicionClimaticaService {
    @Autowired
    private CondicionClimaticaRepository condicionClimaticaRepository;

    @Autowired
    private BitacoraFaenaRepository bitacoraFaenaRepository;

    @Override
    public List<CondicionClimatica> listarTodoCondiciones() {
        return condicionClimaticaRepository.findAll();
    }

    @Override
    public CondicionClimatica insertarCondiciones(CondicionClimatica condicionClimatica) {
        return guardarOActualizar(condicionClimatica);
    }

    @Override
    public CondicionClimatica actualizarCondiciones(CondicionClimatica condicionClimatica) {
        CondicionClimatica foundCondicion = buscarPorId(condicionClimatica.getId());
        if (foundCondicion ==null ) return null;

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
        return condicionClimaticaRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean eliminarCondiciones(Long id) {
        CondicionClimatica encontrada = buscarPorId(id);
        if (encontrada == null) return false;

        condicionClimaticaRepository.deleteById(id);
        return true;
    }

    @Override
    public CondicionClimatica guardarOActualizar(CondicionClimatica condicion) {
        if (condicion.getBitacora() == null || condicion.getBitacora().getId() == null) {
            throw new IllegalArgumentException(
                    "La condición climática debe estar asociada a una bitácora válida"
            );
        }
        boolean bitacoraExiste = bitacoraFaenaRepository
                .existsById(condicion.getBitacora().getId());
        if (!bitacoraExiste) {
            throw new IllegalArgumentException(
                    "La bitácora con id " + condicion.getBitacora().getId() + " no existe"
            );
        }

        // BN-10: si ya existe condición para esa bitácora, actualizar en lugar de crear
        Optional<CondicionClimatica> existente =
                condicionClimaticaRepository.findByBitacoraId(condicion.getBitacora().getId());

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
        return condicionClimaticaRepository.findByBitacoraIdAndUsuarioId(bitacoraId, usuarioId);
    }

    @Override
    public Optional<CondicionClimatica> consultarPorBitacora(Long bitacoraId) {
        return condicionClimaticaRepository.findByBitacoraId(bitacoraId);
    }

    @Override
    public boolean existeCondicionParaBitacora(Long bitacoraId) {
        return condicionClimaticaRepository.existsByBitacoraId(bitacoraId);
    }

    @Override
    public Double obtenerPromedioTemperaturaPorZona(Long zonaId) {
        return condicionClimaticaRepository.promedioTemperaturaPorZona(zonaId);
    }

    @Override
    public List<CondicionClimatica> listarCondicionesPeligrosas(Double umbralVientoNudos) {
        if (umbralVientoNudos == null || umbralVientoNudos <= 0) {
            throw new IllegalArgumentException("El umbral de viento debe ser mayor que cero");
        }
        return condicionClimaticaRepository
                .findByVelocidadVientoNudosGreaterThanEqual(umbralVientoNudos);
    }
}
