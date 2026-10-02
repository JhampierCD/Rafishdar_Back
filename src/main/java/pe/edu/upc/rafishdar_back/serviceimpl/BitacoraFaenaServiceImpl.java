package pe.edu.upc.rafishdar_back.serviceimpl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.BitacoraFaenaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.BitacoraFaenaUpdateDTO;
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

    public BitacoraFaena insertarBitacora(BitacoraFaenaRequestDTO request) {
        User usuario = userRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Embarcacion embarcacion = embarcacionRepository.findById(request.getIdEmbarcacion())
                .orElseThrow(() -> new ResourceNotFoundException("Embarcación no encontrada"));
        ZonaPesca zona = zonaRepository.findById(request.getIdZona())
                .orElseThrow(() -> new ResourceNotFoundException("Zona de pesca no encontrada"));

        BitacoraFaena bitacora = new BitacoraFaena();
        bitacora.setUsuario(usuario);
        bitacora.setEmbarcacion(embarcacion);
        bitacora.setZonaPesca(zona);
        bitacora.setFechaHoraSalida(request.getFechaHoraSalida());
        bitacora.setEstado("Planificada"); // Estado inicial por defecto

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
        bitacora.setEstado("En curso");
        return bitacoraRepository.save(bitacora);
    }

    public BitacoraFaena cambiarCoordenadas(Long idBitacora, Double latitud, Double longitud) {
        BitacoraFaena bitacora = buscarBitacoraPorId(idBitacora);
        bitacora.setLatitudPesca(latitud);
        bitacora.setLongitudPesca(longitud);
        return bitacoraRepository.save(bitacora);
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

    public BitacoraFaena cambiarEstadoAPlanificada(Long idBitacora, BitacoraFaenaUpdateDTO request) {
        BitacoraFaena bitacora = buscarBitacoraPorId(idBitacora);
        if (!"Planificada".equals(bitacora.getEstado())) {
            throw new ConflictException("No se puede editar una faena que ya inició o finalizó");
        }
        bitacora.setFechaHoraSalida(request.getFechaHoraSalida());
        // Lógica adicional para actualizar zona o embarcación si viene en el DTO
        return bitacoraRepository.save(bitacora);
    }

    public void eliminarBitacoraPlanificada(Long idBitacora) {
        BitacoraFaena bitacora = buscarBitacoraPorId(idBitacora);
        if ("Finalizada".equals(bitacora.getEstado())) {
            throw new ConflictException("No se puede eliminar una faena finalizada por motivos de auditoría");
        }
        bitacoraRepository.delete(bitacora);
    }

    // --- MÉTODOS ANALÍTICOS ---

    public List<BitacoraFaena> listarBitacorasPorUsuarioYEstado(Long idUsuario, String estado) {
        return bitacoraRepository.findByUserIdAndEstado(idUsuario, estado);
    }

    public Double obtenerTiempoPromedioFaena(Long idUsuario, LocalDateTime inicio, LocalDateTime fin) {
        Double promedio = bitacoraRepository.calcularTiempoPromedioFaenaMinutos(idUsuario, inicio, fin);
        return promedio != null ? promedio : 0.0;
    }

    // --- MÉTODOS INNOVADORES ---

    public Boolean hayAlertaDeIncidenciasEnZona(Long idZona) {
        LocalDateTime hace48Horas = LocalDateTime.now().minusHours(48);
        List<BitacoraFaena> incidencias = bitacoraRepository.detectarIncidenciasRecientesEnZona(idZona, hace48Horas, "incidencia");
        return !incidencias.isEmpty(); // Retorna true si detectó palabras de peligro
    }
}