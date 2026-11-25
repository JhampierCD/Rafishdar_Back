package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraDTO;
import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.Bitacora;
import pe.edu.upc.rafishdar_back.entities.Faena;
import pe.edu.upc.rafishdar_back.entities.User;
import pe.edu.upc.rafishdar_back.entities.ZonaPesca;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.exceptions.ConflictException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.repositories.BitacoraRepository;
import pe.edu.upc.rafishdar_back.repositories.CondicionClimaticaRepository;
import pe.edu.upc.rafishdar_back.repositories.FaenaRepository;
import pe.edu.upc.rafishdar_back.repositories.UserRepository;
import pe.edu.upc.rafishdar_back.repositories.ZonaPescaRepository;
import pe.edu.upc.rafishdar_back.services.BitacoraService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    UserRepository userRepository;

    @Autowired
    CondicionClimaticaRepository condicionClimaticaRepository;

    @Autowired
    FaenaAuthorizationService autorizacionFaena;

    @Override
    @Transactional(readOnly = true)
    public List<Bitacora> listarTodo() {
        List<Bitacora> resultado = new ArrayList<>();
        for (Bitacora bitacora : bitacoraRepository.findAll()) {
            try {
                autorizacionFaena.validarAcceso(bitacora.getFaena(), false);
                resultado.add(bitacora);
            } catch (AccessDeniedException ignored) {
                // Un pescador no puede consultar bitácoras de faenas ajenas.
            }
        }
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BitacoraDTO> listarTodoDTO() {
        List<Bitacora> bitacoras = listarTodo();
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
    @Transactional(readOnly = true)
    public List<BitacoraDTO> listarBitacorasPorFaena(Long idFaena) {
        Faena faena = faenaRepository.findById(idFaena)
                .orElseThrow(() -> new ResourceNotFoundException("Faena no encontrada"));
        autorizacionFaena.validarAcceso(faena, false);
        List<Bitacora> bitacoras = bitacoraRepository.findByFaenaId(idFaena);
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
        if (request == null) {
            throw new BadRequestException("Los datos de la bitácora son obligatorios");
        }

        if (request.getFaenaId() == null){
            throw new BadRequestException("El ID de la faena es obligatorio");
        }

        Faena foundFaena = faenaRepository.findById(request.getFaenaId())
                .orElseThrow(() -> new ResourceNotFoundException("Faena no encontrada"));

        validarAccesoAFaena(foundFaena);

        if (!validarFaenaEnCurso(foundFaena)){
            throw new ConflictException("No se puede insertar una bitácora en una faena que no esté en curso");
        }

        if ((request.getLatitud() == null) != (request.getLongitud() == null)) {
            throw new BadRequestException("La latitud y longitud deben ingresarse juntas");
        }

        if (request.getLatitud() != null &&
                (!Double.isFinite(request.getLatitud()) ||
                        request.getLatitud() < -90 || request.getLatitud() > 90)) {
            throw new BadRequestException("La latitud debe estar entre -90 y 90");
        }

        if (request.getLongitud() != null &&
                (!Double.isFinite(request.getLongitud()) ||
                        request.getLongitud() < -180 || request.getLongitud() > 180)) {
            throw new BadRequestException("La longitud debe estar entre -180 y 180");
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
    @Transactional(readOnly = true)
    public Bitacora buscarBitacoraPorId(Long id) {
        if (id == null) {
            throw new BadRequestException("El ID de la bitácora es obligatorio");
        }

        Bitacora bitacora = bitacoraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bitácora no encontrada"));
        autorizacionFaena.validarAcceso(bitacora.getFaena(), false);
        return bitacora;
    }

    @Override
    public Bitacora actualizarBitacora(Long idBitacora, BitacoraUpdateDTO request) {
        if (request == null) {
            throw new BadRequestException("Los datos de actualización son obligatorios");
        }

        Bitacora foundBitacora = buscarBitacoraPorId(idBitacora);

        validarAccesoAFaena(foundBitacora.getFaena());

        if (!validarFaenaEnCurso(foundBitacora.getFaena())) {
            throw new ConflictException("No se puede actualizar una bitácora de una faena que no esté en curso");
        }

        if (request.getLatitud() == null &&
                request.getLongitud() == null &&
                request.getObservaciones() == null &&
                request.getZonaPescaId() == null) {
            throw new BadRequestException("Debe ingresar al menos un dato para actualizar");
        }

        if ((request.getLatitud() == null) != (request.getLongitud() == null)) {
            throw new BadRequestException("La latitud y longitud deben ingresarse juntas");
        }

        if (request.getLatitud() != null &&
                (!Double.isFinite(request.getLatitud()) ||
                        request.getLatitud() < -90 || request.getLatitud() > 90)) {
            throw new BadRequestException("La latitud debe estar entre -90 y 90");
        }

        if (request.getLongitud() != null &&
                (!Double.isFinite(request.getLongitud()) ||
                        request.getLongitud() < -180 || request.getLongitud() > 180)) {
            throw new BadRequestException("La longitud debe estar entre -180 y 180");
        }

        if (request.getLatitud() != null) foundBitacora.setLatitudPesca(request.getLatitud());
        if (request.getLongitud() != null) foundBitacora.setLongitudPesca(request.getLongitud());
        if (request.getObservaciones() != null) foundBitacora.setObservaciones(request.getObservaciones());

        if (request.getZonaPescaId() != null) {
            ZonaPesca foundZona = zonaPescaRepository.findById(request.getZonaPescaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Zona Pesca no encontrada"));
            foundBitacora.setZonaPesca(foundZona);
        }

        return bitacoraRepository.save(foundBitacora);
    }

    @Override
    public Boolean eliminarBitacora(Long id) {
        Bitacora foundBitacora = buscarBitacoraPorId(id);

        validarAccesoAFaena(foundBitacora.getFaena());

        if (!validarFaenaEnCurso(foundBitacora.getFaena())) {
            throw new ConflictException("No se puede eliminar una bitácora de una faena que no esté en curso");
        }

        if (condicionClimaticaRepository.existsByBitacoraId(id)) {
            throw new ConflictException("No se puede eliminar la bitácora porque tiene una condición climática asociada");
        }

        bitacoraRepository.delete(foundBitacora);
        return true;
    }

    @Override
    public Boolean validarFaenaEnCurso(Faena faena) {
        return faena != null && "En curso".equalsIgnoreCase(faena.getEstado());
    }

    @Override
    public Boolean verificarDesvioDeZona(Long idBitacora) {
        Bitacora bitacora = buscarBitacoraPorId(idBitacora);

        // Si aún no tiene coordenadas registradas, no hay desvío
        if (bitacora.getLatitudPesca() == null || bitacora.getLongitudPesca() == null) {
            return false;
        }

        ZonaPesca zona = bitacora.getZonaPesca();

        if (zona == null || zona.getLatitud() == null ||
                zona.getLongitud() == null || zona.getRadioAreaKm() == null ||
                !esLatitudValida(zona.getLatitud()) ||
                !esLongitudValida(zona.getLongitud()) ||
                !Double.isFinite(zona.getRadioAreaKm()) || zona.getRadioAreaKm() <= 0 ||
                !esLatitudValida(bitacora.getLatitudPesca()) ||
                !esLongitudValida(bitacora.getLongitudPesca())) {
            throw new BadRequestException("No se puede verificar el desvío porque faltan datos de la zona de pesca");
        }

        // Constante del radio de la Tierra en Kilómetros
        final int R = 6371;

        // Convertimos grados a radianes
        double latDistancia = Math.toRadians(zona.getLatitud() - bitacora.getLatitudPesca());
        double lonDistancia = Math.toRadians(zona.getLongitud() - bitacora.getLongitudPesca());

        double a = Math.sin(latDistancia / 2) * Math.sin(latDistancia / 2)
                + Math.cos(Math.toRadians(bitacora.getLatitudPesca())) * Math.cos(Math.toRadians(zona.getLatitud()))
                * Math.sin(lonDistancia / 2) * Math.sin(lonDistancia / 2);

        a = Math.clamp(a, 0.0, 1.0);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanciaRealKm = R * c; // Distancia calculada en KM

        // Retorna TRUE si la distancia real es mayor al radio permitido de la zona
        return distanciaRealKm > zona.getRadioAreaKm();
    }

    // --- MÉTODOS INNOVADORES ---

    public Boolean hayAlertaDeIncidenciasEnZona(Long idZona, String keyword) {
        if (idZona == null) {
            throw new BadRequestException("El ID de la zona es obligatorio");
        }

        if (keyword == null || keyword.isBlank()) {
            throw new BadRequestException("La palabra clave es obligatoria");
        }

        User usuarioAutenticado = obtenerUsuarioAutenticado();
        zonaPescaRepository.findById(idZona)
                .orElseThrow(() -> new ResourceNotFoundException("Zona de pesca no encontrada"));

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime hace48Horas = ahora.minusHours(48);
        List<Bitacora> incidencias = bitacoraRepository.detectarIncidenciasRecientesEnZona(
                idZona, hace48Horas, ahora, keyword, usuarioAutenticado.getId());
        return !incidencias.isEmpty(); // Retorna true si detectó palabras de peligro
    }

    private void validarAccesoAFaena(Faena faena) {
        User usuario = obtenerUsuarioAutenticado();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ADMIN".equals(authority.getAuthority()));
        if (esAdmin) {
            return;
        }

        boolean esPescador = authentication.getAuthorities().stream()
                .anyMatch(authority -> "PESCADOR".equals(authority.getAuthority()));
        if (!esPescador) {
            throw new AccessDeniedException("No tiene permiso para gestionar bitácoras");
        }

        if (faena == null || faena.getUsuario() == null ||
                !Objects.equals(faena.getUsuario().getId(), usuario.getId())) {
            throw new AccessDeniedException("No puede gestionar bitácoras de una faena ajena");
        }
    }

    private User obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException("Se requiere una sesión autenticada");
        }

        boolean tienePermisoBitacora = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ADMIN".equals(authority.getAuthority()) ||
                        "PESCADOR".equals(authority.getAuthority()));
        if (!tienePermisoBitacora) {
            throw new AccessDeniedException("No tiene permiso para gestionar bitácoras");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }

    private boolean esLatitudValida(Double latitud) {
        return latitud != null && Double.isFinite(latitud) && latitud >= -90 && latitud <= 90;
    }

    private boolean esLongitudValida(Double longitud) {
        return longitud != null && Double.isFinite(longitud) && longitud >= -180 && longitud <= 180;
    }
}