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
        return zonaPescaRepository.findById(id).orElse(null);
    }

    @Override
    public ZonaPesca insertar(ZonaPesca zonaPesca) {
        ZonaPesca foundZonaPesca = zonaPescaRepository.findZonaPescaByNombreZona(zonaPesca.getNombreZona());
        if (foundZonaPesca != null) {
            throw new IllegalArgumentException("Ya existe una zona de pesca con el mismo nombre.");
        }
        return zonaPescaRepository.save(zonaPesca);
    }

    @Override
    public ZonaPesca actualizar(ZonaPesca zonaPesca) {
        ZonaPesca foundZonaPesca = buscarPorId(zonaPesca.getId());
        if (foundZonaPesca == null) {
            return null;
        }
        if (zonaPesca.getNombreZona() == null || zonaPesca.getNombreZona().isEmpty()){
            zonaPesca.setNombreZona(foundZonaPesca.getNombreZona());
        }
        if (zonaPesca.getLatitud() == null){
            zonaPesca.setLatitud(foundZonaPesca.getLatitud());
        }
        if (zonaPesca.getLongitud() == null){
            zonaPesca.setLongitud(foundZonaPesca.getLongitud());
        }
        if (zonaPesca.getDistanciaCostaKm() == null){
            zonaPesca.setDistanciaCostaKm(foundZonaPesca.getDistanciaCostaKm());
        }
        if (zonaPesca.getRadioAreaKm() == null){
            zonaPesca.setRadioAreaKm(foundZonaPesca.getRadioAreaKm());
        }

        if (zonaPescaRepository.findZonaPescaByNombreZona(zonaPesca.getNombreZona())!=null){
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
}
