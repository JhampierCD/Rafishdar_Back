package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.entities.RecomendacionIA;
import pe.edu.upc.rafishdar_back.repositories.RecomendacionIARepository;
import pe.edu.upc.rafishdar_back.services.RecomendacionIAService;

import java.util.List;

@Service
public class RecomendacionIAServiceImpl implements RecomendacionIAService {
    @Autowired
    private RecomendacionIARepository recomendacionIARepository;

    @Override
    public List<RecomendacionIA> listarTodo() {
        return recomendacionIARepository.findAll();
    }

    @Override
    public RecomendacionIA insertarRecomendaciones(RecomendacionIA recomendacionIA) {
        return recomendacionIARepository.save(recomendacionIA);
    }

    @Override
    public RecomendacionIA actualizarRecomendaciones(RecomendacionIA recomendacionIA) {
        RecomendacionIA encontrada = buscarPorId(recomendacionIA.getId());
        if (encontrada == null) {
            return null;
        }

        if (recomendacionIA.getAnalisisTexto() == null) {
            recomendacionIA.setAnalisisTexto(
                    encontrada.getAnalisisTexto()
            );
        }

        if (recomendacionIA.getNivelRiesgo() == null) {
            recomendacionIA.setNivelRiesgo(
                    encontrada.getNivelRiesgo()
            );
        }

        if (recomendacionIA.getFechaGeneracion() == null) {
            recomendacionIA.setFechaGeneracion(
                    encontrada.getFechaGeneracion()
            );
        }

        if (recomendacionIA.getBitacora() == null) {
            recomendacionIA.setBitacora(encontrada.getBitacora());
        }

        return recomendacionIARepository.save(recomendacionIA);
    }

    @Override
    public RecomendacionIA buscarPorId(Long id) {
        return recomendacionIARepository.findById(id).orElse(null);
    }

    @Override
    public Boolean eliminarRecomendaciones(Long id) {
        RecomendacionIA encontrada = buscarPorId(id);

        if (encontrada == null) {
            return false;
        }

        recomendacionIARepository.deleteById(id);
        return true;
    }
}
