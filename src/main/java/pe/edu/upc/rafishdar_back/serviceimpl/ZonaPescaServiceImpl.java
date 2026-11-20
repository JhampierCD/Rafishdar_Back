package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.entities.ZonaPesca;
import pe.edu.upc.rafishdar_back.repositories.ZonaPescaRepository;
import pe.edu.upc.rafishdar_back.services.ZonaPescaService;

import java.util.List;

@Service
public class ZonaPescaServiceImpl implements ZonaPescaService {

    @Autowired
    private ZonaPescaRepository zonaPescaRepository;

    @Override
    public List<ZonaPesca> listarZonaPesca() {
        return zonaPescaRepository.findAll();
    }

    @Override
    public ZonaPesca buscarPorId(Long id) {
        if (id == null) return null;
        return zonaPescaRepository.findById(id).orElse(null);
    }

    @Override
    public ZonaPesca insertar(ZonaPesca zonaPesca) {
        if (zonaPesca == null || zonaPesca.getNombreZona() == null || zonaPesca.getNombreZona().isBlank()) {
            return null;
        }
        ZonaPesca foundZonaPesca = zonaPescaRepository.findZonaPescaByNombreZona(zonaPesca.getNombreZona().trim());
        if (foundZonaPesca != null) {
            return null;
        }
        zonaPesca.setNombreZona(zonaPesca.getNombreZona().trim());
        return zonaPescaRepository.save(zonaPesca);
    }

    @Override
    public ZonaPesca actualizar(ZonaPesca zonaPesca) {
        if (zonaPesca == null || zonaPesca.getId() == null) {
            return null;
        }
        ZonaPesca foundZonaPesca = buscarPorId(zonaPesca.getId());
        if (foundZonaPesca == null) {
            return null;
        }

        if (zonaPesca.getNombreZona() == null || zonaPesca.getNombreZona().isBlank()) {
            zonaPesca.setNombreZona(foundZonaPesca.getNombreZona());
        } else {
            zonaPesca.setNombreZona(zonaPesca.getNombreZona().trim());
        }
        if (zonaPesca.getLatitud() == null) {
            zonaPesca.setLatitud(foundZonaPesca.getLatitud());
        }
        if (zonaPesca.getLongitud() == null) {
            zonaPesca.setLongitud(foundZonaPesca.getLongitud());
        }
        if (zonaPesca.getDistanciaCostaKm() == null) {
            zonaPesca.setDistanciaCostaKm(foundZonaPesca.getDistanciaCostaKm());
        }
        if (zonaPesca.getRadioAreaKm() == null) {
            zonaPesca.setRadioAreaKm(foundZonaPesca.getRadioAreaKm());
        }

        ZonaPesca zona = zonaPescaRepository.findZonaPescaByNombreZona(zonaPesca.getNombreZona());
        if (zona != null && !zona.getId().equals(zonaPesca.getId())) {
            return null;
        }
        return zonaPescaRepository.save(zonaPesca);
    }

    @Override
    public Boolean eliminar(Long id) {
        ZonaPesca foundZonaPesca = buscarPorId(id);
        if (foundZonaPesca == null) {
            return false;
        }
        zonaPescaRepository.deleteById(id);
        return true;
    }

    @Override
    public List<ZonaPesca> listarTop3Historicas(Long especieId, int mes) {
        if (especieId == null || mes < 1 || mes > 12) {
            return List.of();
        }
        return zonaPescaRepository.findTop3ZonasByEspecieAndMesHistorico(especieId, mes);
    }

    @Override
    public List<ZonaPesca> listarCercanasACosta(Double distanciaMaxima) {
        if (distanciaMaxima == null || distanciaMaxima < 0) {
            return List.of();
        }
        return zonaPescaRepository.findZonasCercanasACosta(distanciaMaxima);
    }
}