package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.entities.Embarcacion;
import pe.edu.upc.rafishdar_back.repositories.EmbarcacionRepository;
import pe.edu.upc.rafishdar_back.services.EmbarcacionService;

import java.util.List;

@Service
public class EmbarcacionServiceImpl implements EmbarcacionService {
    @Autowired
    private EmbarcacionRepository embarcacionRepository;

    @Override
    public List<Embarcacion> listarEmbarcaciones() {
        return embarcacionRepository.findAll();
    }

    @Override
    public Embarcacion buscarPorId(Long id) {
        return embarcacionRepository.findById(id).orElse(null);
    }

    @Override
    public Embarcacion insertar(Embarcacion embarcacion) {
        Embarcacion foundEmbarcacion = embarcacionRepository.findEmbarcacionByMatriculaAndNombre(embarcacion.getMatricula(),embarcacion.getNombre());
        if (foundEmbarcacion != null) {
            throw new IllegalArgumentException("Ya existe una embarcación con la misma matrícula y nombre.");
        }
        return embarcacionRepository.save(embarcacion);
    }

    @Override
    public Embarcacion actualizar(Embarcacion embarcacion) {
        Embarcacion foundEmbarcacion= buscarPorId(embarcacion.getId());
        if (foundEmbarcacion == null) {
            return null;
        }
        if (embarcacion.getNombre() == null || embarcacion.getNombre().isEmpty()){
            embarcacion.setNombre(foundEmbarcacion.getNombre());
        }
        if (embarcacion.getMatricula() == null || embarcacion.getMatricula().isEmpty()){
            embarcacion.setMatricula(foundEmbarcacion.getMatricula());
        }
        if (embarcacion.getCapacidadToneladas() == null){
            embarcacion.setCapacidadToneladas(foundEmbarcacion.getCapacidadToneladas());
        }

        if (embarcacionRepository.findEmbarcacionByMatriculaAndNombre(embarcacion.getMatricula(),embarcacion.getNombre())!=null){
           return null;
        }
        return embarcacionRepository.save(embarcacion);
    }

    @Override
    public Boolean eliminar(Long id) {
        Embarcacion foundEmbarcacion = buscarPorId(id);
        if (foundEmbarcacion == null) {
            return false;
        }
        embarcacionRepository.deleteById(id);
        return true;
    }
}
