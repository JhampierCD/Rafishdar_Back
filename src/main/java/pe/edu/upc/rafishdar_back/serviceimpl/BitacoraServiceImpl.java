package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.CondicionClimaticaDTO;
import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraDTO;
import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraUpdateDTO;
import pe.edu.upc.rafishdar_back.dtos.detalle_captura.DetalleCapturaDTO;
import pe.edu.upc.rafishdar_back.entities.Bitacora;
import pe.edu.upc.rafishdar_back.entities.Faena;
import pe.edu.upc.rafishdar_back.entities.ZonaPesca;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.exceptions.DuplicateResourceException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.repositories.BitacoraRepository;
import pe.edu.upc.rafishdar_back.repositories.DetalleCapturaRepository;
import pe.edu.upc.rafishdar_back.repositories.FaenaRepository;
import pe.edu.upc.rafishdar_back.repositories.ZonaPescaRepository;
import pe.edu.upc.rafishdar_back.services.BitacoraService;
import pe.edu.upc.rafishdar_back.services.DetalleCapturaService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BitacoraServiceImpl implements BitacoraService {

    @Autowired
    BitacoraRepository bitacoraRepository;

    @Autowired
    ZonaPescaRepository zonaPescaRepository;

    @Autowired
    FaenaRepository faenaRepository;

    @Autowired
    DetalleCapturaService capturaService;

    @Override
    public List<Bitacora> listarTodo() {
        return bitacoraRepository.findAll();
    }

    @Override
    public List<BitacoraDTO> listarTodoDTO() {
        List<Bitacora> bitacoras = bitacoraRepository.findAll();
        List<BitacoraDTO> dtos = new ArrayList<>();

        for (Bitacora bitacora : bitacoras){
            BitacoraDTO dto = new BitacoraDTO();

            dto.setId(bitacora.getId());
            dto.setLatitud(bitacora.getLatitudPesca());
            dto.setLongitud(bitacora.getLongitudPesca());
            dto.setFechaHoraRegistro(bitacora.getFechaHoraRegistro());
            dto.setObservaciones(bitacora.getObservaciones());
            dto.setFaenaId(bitacora.getFaena().getId());
            dto.setZonaPescaId(bitacora.getZonaPesca() != null ? bitacora.getZonaPesca().getId() : null);

            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public Bitacora insertarBitacora(BitacoraRequestDTO request) {
        if (request.getFaenaId() == null){
            throw new BadRequestException("El ID de la faena es obligatorio");
        }

        Faena foundFaena = faenaRepository.findById(request.getFaenaId())
                .orElseThrow(() -> new ResourceNotFoundException("Faena no encontrada"));

        if (!validarFaenaEnCurso(foundFaena)){
            throw new BadRequestException("No se puede insertar una bitácora en una faena que no esté en curso");
        }

        ZonaPesca zonaAsignada;

        // Lógica de validación / herencia de zona de pesca
        if (request.getZonaPescaId() != null) {
            // El pescador proporcionó una zona explícita
            zonaAsignada = zonaPescaRepository.findById(request.getZonaPescaId())
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró la zona con ID: " + request.getZonaPescaId()));
        } else {
            // El campo vino nulo: buscamos el último log registrado de esta faena
            Optional<Bitacora> ultimaBitacora = bitacoraRepository.findFirstByFaenaIdOrderByFechaHoraRegistroDesc(request.getFaenaId());

            if (ultimaBitacora.isPresent() && ultimaBitacora.get().getZonaPesca() != null) {
                // Hereda la zona de la bitácora anterior
                zonaAsignada = ultimaBitacora.get().getZonaPesca();
            } else {
                // Es el primer log y no contiene zona: error de negocio
                throw new BadRequestException("La primera bitácora de la faena debe especificar obligatoriamente una zona de pesca.");
            }
        }

        Bitacora bitacora = new Bitacora();
        bitacora.setLatitudPesca(request.getLatitud());
        bitacora.setLongitudPesca(request.getLongitud());
        bitacora.setFechaHoraRegistro(LocalDateTime.now());
        bitacora.setObservaciones(request.getObservaciones());
        bitacora.setZonaPesca(zonaAsignada);
        bitacora.setFaena(foundFaena);

        return bitacoraRepository.save(bitacora);
    }

    @Override
    public Bitacora buscarBitacoraPorId(Long id) {
        return bitacoraRepository.findById(id).orElse(null);
    }

    @Override
    public Bitacora actualizarBitacora(BitacoraUpdateDTO request) {
        Bitacora foundBitacora = buscarBitacoraPorId(request.getId());

        if (foundBitacora == null){
            throw new ResourceNotFoundException("Bitacora no encontrada. No se puede actualizar");
        }

        if (!validarFaenaEnCurso(foundBitacora.getFaena())){
            throw new BadRequestException("No se puede actualizar una bitácora de una faena que no esté en curso");
        }

        if (request.getLatitud() != null) foundBitacora.setLatitudPesca(request.getLatitud());
        if (request.getLongitud() != null) foundBitacora.setLongitudPesca(request.getLongitud());
        if (request.getObservaciones() != null) foundBitacora.setObservaciones(request.getObservaciones());
        if (request.getZonaPescaId() != null){
            ZonaPesca foundZona = zonaPescaRepository.findById(request.getZonaPescaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Zona Pesca no encontrada"));
            foundBitacora.setZonaPesca(foundZona);
        }

        return bitacoraRepository.save(foundBitacora);
    }

    @Override
    public void eliminarBitacora(Long id) {
        if (id == null || !bitacoraRepository.existsById(id)){
            throw new ResourceNotFoundException("Bitacora no encontrada. No se puede eliminar");
        }

        Bitacora foundBitacora = buscarBitacoraPorId(id);

        if (!validarFaenaEnCurso(foundBitacora.getFaena())){
            throw new BadRequestException("No se puede eliminar una bitácora de una faena que no esté en curso");
        }

        bitacoraRepository.deleteById(id);
    }

    @Override
    public Boolean validarFaenaEnCurso(Faena faena) {
        if (!"En curso".equalsIgnoreCase(faena.getEstado())) {
            return false; // La faena no está en curso
        }
        return true; // La faena está en curso
    }

    @Override
    public Boolean verificarDesvioDeZona(Long idBitacora) {
        Bitacora bitacora = buscarBitacoraPorId(idBitacora);

        // Si aún no tiene coordenadas registradas, no hay desvío
        if (bitacora.getLatitudPesca() == null || bitacora.getLongitudPesca() == null) {
            return false;
        }

        ZonaPesca zona = bitacora.getZonaPesca();

        // Constante del radio de la Tierra en Kilómetros
        final int R = 6371;

        // Convertimos grados a radianes
        double latDistancia = Math.toRadians(zona.getLatitud() - bitacora.getLatitudPesca());
        double lonDistancia = Math.toRadians(zona.getLongitud() - bitacora.getLongitudPesca());

        double a = Math.sin(latDistancia / 2) * Math.sin(latDistancia / 2)
                + Math.cos(Math.toRadians(bitacora.getLatitudPesca())) * Math.cos(Math.toRadians(zona.getLatitud()))
                * Math.sin(lonDistancia / 2) * Math.sin(lonDistancia / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanciaRealKm = R * c; // Distancia calculada en KM

        // Retorna TRUE si la distancia real es mayor al radio permitido de la zona
        return distanciaRealKm > zona.getRadioAreaKm();
    }

    // --- MÉTODOS INNOVADORES ---

    public Boolean hayAlertaDeIncidenciasEnZona(Long idZona, String keyword) {
        LocalDateTime hace48Horas = LocalDateTime.now().minusHours(48);
        List<Bitacora> incidencias = bitacoraRepository.detectarIncidenciasRecientesEnZona(idZona, hace48Horas, keyword);
        return !incidencias.isEmpty(); // Retorna true si detectó palabras de peligro
    }
}
