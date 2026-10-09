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
        if (embarcacion == null) {
            return null;
        }
        if (existeMatricula(embarcacion.getMatricula())) {
            return null;
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

        if (embarcacion.getMatricula() != null &&
                !embarcacion.getMatricula().equals(foundEmbarcacion.getMatricula()) &&
                existeMatricula(embarcacion.getMatricula())) {
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

    @Override
    public List<Embarcacion> listarPorUsuario(Long usuarioId) {
        if (usuarioId == null) {
            return List.of();
        }
        return embarcacionRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public boolean existeMatricula(String matricula) {
        return matricula != null && !matricula.isBlank() &&
                embarcacionRepository.existsByMatricula(matricula.trim());
    }
}
