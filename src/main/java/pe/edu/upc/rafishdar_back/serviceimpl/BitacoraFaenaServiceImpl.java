package pe.edu.upc.rafishdar_back.serviceimpl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.entities.BitacoraFaena;
import pe.edu.upc.rafishdar_back.entities.Embarcacion;
import pe.edu.upc.rafishdar_back.entities.User;
import pe.edu.upc.rafishdar_back.entities.ZonaPesca;
import pe.edu.upc.rafishdar_back.exceptions.ConflictException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.repositories.BitacoraFaenaRepository;
import pe.edu.upc.rafishdar_back.repositories.EmbarcacionRepository;
import pe.edu.upc.rafishdar_back.repositories.UserRepository;
import pe.edu.upc.rafishdar_back.repositories.ZonaPescaRepository;
import pe.edu.upc.rafishdar_back.services.BitacoraFaenaService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BitacoraFaenaServiceImpl implements BitacoraFaenaService {

    @Autowired
    private BitacoraFaenaRepository bitacoraRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmbarcacionRepository embarcacionRepository;

    @Autowired
    private ZonaPescaRepository zonaRepository;

    // --- MÉTODOS BASE (CRUD) ---

    public BitacoraFaena insertarBitacora(BitacoraFaena request) {
        User usuario = userRepository.findById(request.getUsuario().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Embarcacion embarcacion = embarcacionRepository.findById(request.getEmbarcacion().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Embarcación no encontrada"));
        ZonaPesca zona = zonaRepository.findById(request.getZonaPesca().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Zona de pesca no encontrada"));

        BitacoraFaena bitacora = new BitacoraFaena();
        bitacora.setUsuario(usuario);
        bitacora.setEmbarcacion(embarcacion);
        bitacora.setZonaPesca(zona);
        bitacora.setFechaHoraSalida(request.getFechaHoraSalida());
        bitacora.setEstado("Planificada"); // Estado inicial por defecto

        if(request.getFechaHoraLlegada() != null) {
            bitacora.setFechaHoraLlegada(request.getFechaHoraLlegada());
        }

        return bitacoraRepository.save(bitacora);
    }

    public BitacoraFaena buscarBitacoraPorId(Long idBitacora) {
        return bitacoraRepository.findById(idBitacora)
                .orElseThrow(() -> new ResourceNotFoundException("Bitácora no encontrada"));
    }

    public List<BitacoraFaena> listarBitacorasPorUsuarioId(Long idUsuario) {
        return bitacoraRepository.findByUserId(idUsuario);
    }

    public BitacoraFaena cambiarEstadoAEnCurso(Long idBitacora) {
        BitacoraFaena bitacora = buscarBitacoraPorId(idBitacora);
        if (!"Planificada".equals(bitacora.getEstado())) {
            throw new ConflictException("Solo las faenas planificadas pueden pasar a 'En curso'");
        }
        bitacora.setFechaHoraSalida(LocalDateTime.now());
        bitacora.setEstado("En curso");
        return bitacoraRepository.save(bitacora);
    }

    @Override
    public BitacoraFaena cambiarEstadoAPlanificada(Long idBitacora) {
        BitacoraFaena bitacora = buscarBitacoraPorId(idBitacora);

        if (!"En curso".equals(bitacora.getEstado())) {
            throw new ConflictException("Solo se puede revertir a 'Planificada' una faena 'En curso'.");
        }

        bitacora.setEstado("Planificada");
        // fechaHoraSalida se conserva intacta con el valor que ya tiene en BD

        return bitacoraRepository.save(bitacora);
    }

    @Override
    public BitacoraFaena actualizarPlanificada(Long idBitacora, BitacoraFaena datosNuevos) {
        BitacoraFaena bitacoraBD = buscarBitacoraPorId(idBitacora);

        if (!"Planificada".equals(bitacoraBD.getEstado())) {
            throw new ConflictException("Este método solo permite actualizar faenas en estado 'Planificada'.");
        }

        // 1. Actualizamos solo lo permitido en este estado
        if (datosNuevos.getFechaHoraSalida() != null) {
            bitacoraBD.setFechaHoraSalida(datosNuevos.getFechaHoraSalida());
        }

        // Ignoramos si el JSON trae 'estado', 'fechaHoraLlegada', 'latitud', etc.
        // Solo actualizamos las FK si vienen en el objeto
        if (datosNuevos.getZonaPesca() != null && datosNuevos.getZonaPesca().getId() != null) {
            ZonaPesca nuevaZona = zonaRepository.findById(datosNuevos.getZonaPesca().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Nueva zona no encontrada"));
            bitacoraBD.setZonaPesca(nuevaZona);
        }

        if (datosNuevos.getEmbarcacion() != null && datosNuevos.getEmbarcacion().getId() != null) {
            Embarcacion nuevaEmbarcacion = embarcacionRepository.findById(datosNuevos.getEmbarcacion().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Nueva embarcación no encontrada"));
            bitacoraBD.setEmbarcacion(nuevaEmbarcacion);
        }

        return bitacoraRepository.save(bitacoraBD);
    }

    @Override
    public BitacoraFaena actualizarEnCurso(Long idBitacora, BitacoraFaena datosNuevos) {
        BitacoraFaena bitacoraBD = buscarBitacoraPorId(idBitacora);

        if (!"En curso".equals(bitacoraBD.getEstado())) {
            throw new ConflictException("Este método solo permite actualizar faenas en estado 'En curso'.");
        }

        // 1. Actualizamos solo lo permitido en este estado (Coordenadas y Observaciones)
        if (datosNuevos.getLatitudPesca() != null) {
            bitacoraBD.setLatitudPesca(datosNuevos.getLatitudPesca());
        }
        if (datosNuevos.getLongitudPesca() != null) {
            bitacoraBD.setLongitudPesca(datosNuevos.getLongitudPesca());
        }
        if (datosNuevos.getObservaciones() != null) {
            bitacoraBD.setObservaciones(datosNuevos.getObservaciones());
        }

        // Ignoramos si intentan cambiar la Zona, la Embarcación o las fechas aquí.
        return bitacoraRepository.save(bitacoraBD);
    }

    public BitacoraFaena terminarBitacora(Long idBitacora, String observaciones) {
        BitacoraFaena bitacora = buscarBitacoraPorId(idBitacora);
        if (!"En curso".equals(bitacora.getEstado())) {
            throw new ConflictException("Solo las faenas 'En curso' pueden finalizarse");
        }
        bitacora.setEstado("Finalizada");
        bitacora.setFechaHoraLlegada(LocalDateTime.now());
        bitacora.setObservaciones(observaciones);
        return bitacoraRepository.save(bitacora);
    }

    public void eliminarBitacoraPlanificada(Long idBitacora) {
        BitacoraFaena bitacora = buscarBitacoraPorId(idBitacora);
        if ("Finalizada".equals(bitacora.getEstado())) {
            throw new ConflictException("No se puede eliminar una faena finalizada por motivos de auditoría");
        }
        bitacoraRepository.delete(bitacora);
    }

    @Override
    @Scheduled(fixedRate = 3600000) // Cada hora
    public void automatizarCancelacionFaenasExpiradas() {
        LocalDateTime limite = LocalDateTime.now().minusHours(24); // Faenas planificadas que no iniciaron en 24 horas
        int cantidadCanceladas = bitacoraRepository.cancelarFaenasExpiradas(limite);
        System.out.println("Se cancelaron " + cantidadCanceladas + " faenas expiradas automáticamente.");
    }

    // --- MÉTODOS ANALÍTICOS ---

    public List<BitacoraFaena> listarBitacorasPorUsuarioYEstado(Long idUsuario, String estado) {
        return bitacoraRepository.findByUserIdAndEstado(idUsuario, estado);
    }

    public Double obtenerTiempoPromedioFaena(Long idUsuario, LocalDateTime inicio, LocalDateTime fin) {
        Double promedio = bitacoraRepository.calcularTiempoPromedioFaenaMinutos(idUsuario, inicio, fin);
        return promedio != null ? promedio : 0.0;
    }

    @Override
    public boolean verificarDesvioDeZona(Long idBitacora) {
        BitacoraFaena bitacora = buscarBitacoraPorId(idBitacora);

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

    public Boolean hayAlertaDeIncidenciasEnZona(Long idZona) {
        LocalDateTime hace48Horas = LocalDateTime.now().minusHours(48);
        List<BitacoraFaena> incidencias = bitacoraRepository.detectarIncidenciasRecientesEnZona(idZona, hace48Horas, "incidencia");
        return !incidencias.isEmpty(); // Retorna true si detectó palabras de peligro
    }
}