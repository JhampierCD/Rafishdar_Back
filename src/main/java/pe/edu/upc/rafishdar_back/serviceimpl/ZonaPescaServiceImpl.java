package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.entities.ZonaPesca;
import pe.edu.upc.rafishdar_back.exceptions.ConflictException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
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
        if (id == null) {
            throw new BadRequestException("El ID de zona de pesca es obligatorio");
        }
        return zonaPescaRepository.findById(id).orElse(null);
    }

    @Override
    public ZonaPesca insertar(ZonaPesca zonaPesca) {
        validarCoordenadasYRadio(zonaPesca);

        ZonaPesca foundZonaPesca = zonaPescaRepository.findZonaPescaByNombreZona(zonaPesca.getNombreZona());
        if (foundZonaPesca != null) {
            throw new ConflictException("Ya existe una zona de pesca con el mismo nombre.");
        }
        validarDistanciaCosta(zonaPesca.getDistanciaCostaKm());
        return zonaPescaRepository.save(zonaPesca);
    }

    @Override
    public ZonaPesca actualizar(ZonaPesca zonaPesca) {
        if (zonaPesca == null || zonaPesca.getId() == null) {
            throw new BadRequestException("El ID de la zona de pesca es obligatorio");
        }

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

        validarCoordenadasYRadio(zonaPesca);
        validarDistanciaCosta(zonaPesca.getDistanciaCostaKm());

        ZonaPesca zonaConMismoNombre = zonaPescaRepository.findZonaPescaByNombreZona(zonaPesca.getNombreZona());
        if (zonaConMismoNombre != null && !zonaConMismoNombre.getId().equals(zonaPesca.getId())){
           throw new ConflictException("Ya existe una zona de pesca con el mismo nombre.");
        }
        return zonaPescaRepository.save(zonaPesca);
    }

    @Override
    public Boolean eliminar(Long id) {
        ZonaPesca foundZonaPesca = buscarPorId(id);
        if (foundZonaPesca == null) {
            throw new ResourceNotFoundException("Zona de pesca no encontrada");
        }
        if (foundZonaPesca.getBitacoras() != null && !foundZonaPesca.getBitacoras().isEmpty()) {
            throw new ConflictException("No se puede eliminar una zona asociada a bitácoras");
        }
        zonaPescaRepository.delete(foundZonaPesca);
        return true;
    }

    @Override
    public List<ZonaPesca> listarTop3Historicas(Long especieId, int mes) {
        if (especieId == null || mes < 1 || mes > 12) {
            throw new BadRequestException("El ID de especie y un mes entre 1 y 12 son obligatorios");
        }
        return zonaPescaRepository.findTop3ZonasByEspecieAndMesHistorico(especieId, mes);
    }

    @Override
    public List<ZonaPesca> listarCercanasACosta(Double distanciaMaxima) {
        if (distanciaMaxima == null || !Double.isFinite(distanciaMaxima) || distanciaMaxima < 0) {
            throw new BadRequestException("La distancia máxima debe ser no negativa y finita");
        }
        return zonaPescaRepository.findZonasCercanasACosta(distanciaMaxima);
    }

    private void validarCoordenadasYRadio(ZonaPesca zonaPesca) {
        if (zonaPesca == null) {
            throw new BadRequestException("Los datos de la zona de pesca son obligatorios");
        }

        Double latitud = zonaPesca.getLatitud();
        if (latitud == null || !Double.isFinite(latitud) || latitud < -90 || latitud > 90) {
            throw new BadRequestException("La latitud de la zona debe estar entre -90 y 90");
        }

        Double longitud = zonaPesca.getLongitud();
        if (longitud == null || !Double.isFinite(longitud) || longitud < -180 || longitud > 180) {
            throw new BadRequestException("La longitud de la zona debe estar entre -180 y 180");
        }

        Double radioAreaKm = zonaPesca.getRadioAreaKm();
        if (radioAreaKm == null || !Double.isFinite(radioAreaKm) || radioAreaKm <= 0) {
            throw new BadRequestException("El radio de la zona debe ser positivo y finito");
        }
    }

    private void validarDistanciaCosta(Double distanciaCostaKm) {
        if (distanciaCostaKm == null || !Double.isFinite(distanciaCostaKm) || distanciaCostaKm < 0) {
            throw new BadRequestException("La distancia a costa debe ser no negativa y finita");
        }
    }
}
