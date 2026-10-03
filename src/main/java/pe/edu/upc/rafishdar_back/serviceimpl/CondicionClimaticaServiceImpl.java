package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.CondicionClimaticaDTO;
import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;
import pe.edu.upc.rafishdar_back.repositories.CondicionClimaticaRepository;
import pe.edu.upc.rafishdar_back.services.CondicionClimaticaService;

import java.util.ArrayList;
import java.util.List;

@Service
public class CondicionClimaticaServiceImpl implements CondicionClimaticaService {
    @Autowired
    private CondicionClimaticaRepository condicionClimaticaRepository;

    @Override
    public List<CondicionClimatica> listarTodoCondiciones() {
        return condicionClimaticaRepository.findAll();
    }

    @Override
    public CondicionClimatica insertarCondiciones(CondicionClimatica condicionClimatica) {
        return condicionClimaticaRepository.save(condicionClimatica);
    }

    @Override
    public CondicionClimatica actualizarCondiciones(CondicionClimatica condicionClimatica) {
        CondicionClimatica foundCondicion = buscarPorId(condicionClimatica.getId());
        if (foundCondicion ==null ) {
            return null;
        }

        if (condicionClimatica.getTemperaturaCelsius() == null) {
            condicionClimatica.setTemperaturaCelsius(
                    foundCondicion.getTemperaturaCelsius()
            );
        }

        if (condicionClimatica.getVelocidadVientoNudos() == null) {
            condicionClimatica.setVelocidadVientoNudos(
                    foundCondicion.getVelocidadVientoNudos()
            );
        }

        if (condicionClimatica.getEstadoOleaje() == null) {
            condicionClimatica.setEstadoOleaje(
                    foundCondicion.getEstadoOleaje()
            );
        }

        if (condicionClimatica.getBitacora() == null) {
            condicionClimatica.setBitacora(condicionClimatica.getBitacora());
        }

        return condicionClimaticaRepository.save(condicionClimatica);
    }

    @Override
    public CondicionClimatica buscarPorId(Long id) {
        return condicionClimaticaRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean eliminarCondiciones(Long id) {
        CondicionClimatica encontrada = buscarPorId(id);

        if (encontrada == null) {
            return false;
        }

        condicionClimaticaRepository.deleteById(id);
        return true;
    }
}
