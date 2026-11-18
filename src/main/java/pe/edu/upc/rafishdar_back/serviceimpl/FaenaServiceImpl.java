package pe.edu.upc.rafishdar_back.serviceimpl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.faena.FaenaDTO;
import pe.edu.upc.rafishdar_back.dtos.faena.FaenaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.faena.FaenaUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.*;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.exceptions.ConflictException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.repositories.*;
import pe.edu.upc.rafishdar_back.services.BitacoraService;
import pe.edu.upc.rafishdar_back.services.FaenaService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FaenaServiceImpl implements FaenaService {

    @Autowired
    private FaenaRepository faenaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmbarcacionRepository embarcacionRepository;

    @Autowired
    private BitacoraService bitacoraService;

    @Autowired
    private GastoOperativoRepository gastoOperativoRepository;

    @Autowired
    private RecomendacionIARepository recomendacionIARepository;

    // --- MÉTODOS BASE (CRUD) ---

    @Override
    public List<Faena> listarTodo() {
        return faenaRepository.findAll();
    }

    @Override
    public List<FaenaDTO> listarTodoDTO() {
        List<Faena> faenas = faenaRepository.findAll();
        List<FaenaDTO> listaDTOs = new ArrayList<>();

        for (Faena faena : faenas){
            FaenaDTO faenaDTO = new FaenaDTO();

            faenaDTO.setId(faena.getId());
            faenaDTO.setFechaHoraSalida(faena.getFechaHoraSalida());
            faenaDTO.setFechaHoraLlegada(faena.getFechaHoraLlegada());
            faenaDTO.setEstado(faena.getEstado());
            faenaDTO.setUsuarioId(faena.getUsuario().getId());
            faenaDTO.setEmbarcacionId(faena.getEmbarcacion().getId());

            listaDTOs.add(faenaDTO);
        }

        return listaDTOs;
    }

    @Override
    public List<FaenaDTO> listarFaenasPorUsuarioId(Long idUsuario) {
        List<Faena> faenas = faenaRepository.findByUsuarioId(idUsuario);
        List<FaenaDTO> listaDTOs = new ArrayList<>();

        for (Faena faena : faenas){
            FaenaDTO faenaDTO = new FaenaDTO();

            faenaDTO.setId(faena.getId());
            faenaDTO.setFechaHoraSalida(faena.getFechaHoraSalida());
            faenaDTO.setFechaHoraLlegada(faena.getFechaHoraLlegada());
            faenaDTO.setEstado(faena.getEstado());
            faenaDTO.setUsuarioId(faena.getUsuario().getId());
            faenaDTO.setEmbarcacionId(faena.getEmbarcacion().getId());

            listaDTOs.add(faenaDTO);
        }

        return listaDTOs;
    }

    @Override
    public Faena buscarFaenaPorId(Long idFaena) {
        return faenaRepository.findById(idFaena)
                .orElseThrow(() -> new ResourceNotFoundException("Faena no encontrada"));
    }

    @Override
    public Faena insertarFaena(FaenaRequestDTO request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BadRequestException("No se encontró una sesión de usuario autenticada.");
        }

        String emailUsuario = auth.getName();
        User usuario = userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Embarcacion embarcacion = embarcacionRepository.findById(request.getEmbarcacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Embarcación no encontrada"));

        if (request.getFechaHoraSalida() == null){
            throw new ResourceNotFoundException("Fecha y Hora de Salida no ingresadas");
        }

        if (request.getFechaHoraLlegada() != null) {
            if (!request.getFechaHoraLlegada().isAfter(request.getFechaHoraSalida())){
                throw new ConflictException("La fecha y hora de llegada no puede ser anterior a la fecha de salida");
            }

            if (faenaRepository.existeFaenaSolapada(usuario.getId(), embarcacion.getId(), request.getFechaHoraSalida(),
                    request.getFechaHoraSalida())){
                throw new ConflictException("El usuario o la embarcación ya tienen otra faena en ese horario");
            }
        }

        Faena faena = new Faena();
        faena.setUsuario(usuario);
        faena.setEmbarcacion(embarcacion);
        faena.setFechaHoraSalida(request.getFechaHoraSalida());
        faena.setFechaHoraLlegada(request.getFechaHoraLlegada());
        faena.setEstado("Planificada"); // Estado inicial por defecto

        return faenaRepository.save(faena);
    }

    @Override
    public Faena cambiarEstadoAEnCurso(Long idFaena) {
        Faena faena = buscarFaenaPorId(idFaena);
        if (!"Planificada".equals(faena.getEstado())) {
            throw new ConflictException("Solo las faenas planificadas pueden pasar a 'En curso'");
        }
        faena.setFechaHoraSalida(LocalDateTime.now());
        faena.setEstado("En curso");
        return faenaRepository.save(faena);
    }

    @Override
    public Faena cambiarEstadoAPlanificada(Long idFaena) {
        Faena faena = buscarFaenaPorId(idFaena);

        if (!"En curso".equals(faena.getEstado())) {
            throw new ConflictException("Solo se puede revertir a 'Planificada' una faena 'En curso'.");
        }

        if (faena.getBitacoras() != null && !faena.getBitacoras().isEmpty()) {
            throw new ConflictException("No se puede revertir a 'Planificada' una faena que ya tiene bitácoras registradas.");
        }

        if (!faenaRepository.existeFaenaSolapada(faena.getUsuario().getId(), faena.getEmbarcacion().getId(), LocalDateTime.now().plusHours(1),
                faena.getFechaHoraLlegada())){
            // Ajuste de la fecha de salida para evitar conflictos con faenas solapadas
            faena.setFechaHoraSalida(LocalDateTime.now().plusHours(1));
        }

        faena.setEstado("Planificada");
        // fechaHoraSalida se conserva intacta con el valor que ya tiene en BD

        return faenaRepository.save(faena);
    }

    @Override
    public Faena actualizarPlanificada(Long idFaena, FaenaUpdateDTO datosNuevos) {
        Faena faena = buscarFaenaPorId(idFaena);

        if (!"Planificada".equals(faena.getEstado())) {
            throw new ConflictException("Este método solo permite actualizar faenas en estado 'Planificada'.");
        }

        // 1. Actualizamos solo lo permitido en este estado
        User usuario = userRepository.findById(faena.getUsuario().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Embarcacion embarcacion = new Embarcacion();

        if (datosNuevos.getEmbarcacionId() != null){
            embarcacion = embarcacionRepository.findById(datosNuevos.getEmbarcacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Embarcación no encontrada"));

            faena.setEmbarcacion(embarcacion);
        }
        else{
            embarcacion = faena.getEmbarcacion();
        }

        if (datosNuevos.getFechaHoraSalida() != null && datosNuevos.getFechaHoraLlegada() != null){
            if (!datosNuevos.getFechaHoraLlegada().isAfter(datosNuevos.getFechaHoraSalida())){
                throw new ConflictException("La fecha y hora de llegada no puede ser anterior a la fecha de salida");
            }

            if (faenaRepository.existeFaenaSolapada(usuario.getId(), embarcacion.getId(), datosNuevos.getFechaHoraSalida(),
                    datosNuevos.getFechaHoraLlegada())){
                throw new ConflictException("El usuario o la embarcación ya tienen otra faena en ese horario");
            }

            faena.setFechaHoraSalida(datosNuevos.getFechaHoraSalida());
            faena.setFechaHoraLlegada(datosNuevos.getFechaHoraLlegada());
        }

        if (datosNuevos.getFechaHoraSalida() == null){
            if (faenaRepository.existeFaenaSolapada(usuario.getId(), embarcacion.getId(), faena.getFechaHoraSalida(),
                    datosNuevos.getFechaHoraLlegada())){
                throw new ConflictException("El usuario o la embarcación ya tienen otra faena en ese horario");
            }
            faena.setFechaHoraLlegada(datosNuevos.getFechaHoraLlegada());
        }

        if (datosNuevos.getFechaHoraLlegada() == null){
            if (faenaRepository.existeFaenaSolapada(usuario.getId(), embarcacion.getId(), datosNuevos.getFechaHoraSalida(),
                    faena.getFechaHoraLlegada())){
                throw new ConflictException("El usuario o la embarcación ya tienen otra faena en ese horario");
            }
            faena.setFechaHoraSalida(datosNuevos.getFechaHoraSalida());
        }

        // Ignoramos si el JSON trae 'user'.
        return faenaRepository.save(faena);
    }

    @Override
    public Faena actualizarEnCurso(Long idFaena, FaenaUpdateDTO datosNuevos) {
        Faena foundFaena = buscarFaenaPorId(idFaena);

        if (!"En curso".equals(foundFaena.getEstado())) {
            throw new ConflictException("Este método solo permite actualizar faenas en estado 'En curso'.");
        }

        // 1. Actualizamos solo lo permitido en este estado (Coordenadas y Observaciones)
        if (datosNuevos.getFechaHoraLlegada() != null) {
            User usuario = userRepository.findById(foundFaena.getUsuario().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            Embarcacion embarcacion = embarcacionRepository.findById(foundFaena.getEmbarcacion().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Embarcación no encontrada"));

            if (datosNuevos.getFechaHoraLlegada().isBefore(foundFaena.getFechaHoraSalida())) {
                throw new ConflictException("La fecha y hora de llegada no puede ser anterior a la fecha de salida");
            }

            if (faenaRepository.existeFaenaSolapada(usuario.getId(), embarcacion.getId(), foundFaena.getFechaHoraSalida(),
                    datosNuevos.getFechaHoraLlegada())){
                throw new ConflictException("El usuario o la embarcación ya tienen otra faena en ese horario");
            }

            foundFaena.setFechaHoraLlegada(datosNuevos.getFechaHoraLlegada());
        }

        // Ignoramos si intentan cambiar el User, la Embarcación o el estado aquí.
        return faenaRepository.save(foundFaena);
    }

    @Override
    public Faena terminarFaena(Long idFaena, Boolean ultimaBitacora, BitacoraRequestDTO request) {
        Faena faena = buscarFaenaPorId(idFaena);
        if (!"En curso".equals(faena.getEstado())) {
            throw new ConflictException("Solo las faenas 'En curso' pueden finalizarse");
        }

        // Implementación de crear última bitácora si es necesario
        if (ultimaBitacora){
            if (request == null){
                throw new BadRequestException("Se requiere información de la última bitácora para finalizar la faena");
            }

            try{
                Bitacora bitacora = bitacoraService.insertarBitacora(request);
            } catch (Exception e) {
                throw new ConflictException("Error al crear la última bitácora: " + e.getMessage());
            }
        }

        faena.setEstado("Finalizada");
        faena.setFechaHoraLlegada(LocalDateTime.now());
        return faenaRepository.save(faena);
    }

    @Override
    public Faena cancelarFaenaEnCursoOFinalizada(Long idFaena) {
        Faena foundFaena = buscarFaenaPorId(idFaena);
        if (!"En curso".equals(foundFaena.getEstado()) && !"Finalizada".equals(foundFaena.getEstado())) {
            throw new ConflictException("Solo las faenas 'En curso' o 'Finalizada' pueden cancelarse");
        }
        foundFaena.setEstado("Cancelada");
        return faenaRepository.save(foundFaena);
    }

    @Override
    public void eliminarFaenaPlanificada(Long idFaena) {
        Faena foundFaena = buscarFaenaPorId(idFaena);
        if (!"Planificada".equals(foundFaena.getEstado())) {
            throw new ConflictException("No se puede eliminar una faena finalizada o en curso por motivos de auditoría");
        }

        if (foundFaena.getGastoOperativo() != null){
            gastoOperativoRepository.delete(foundFaena.getGastoOperativo());
        }

        if (foundFaena.getRecomendacionIA() != null){
            recomendacionIARepository.delete(foundFaena.getRecomendacionIA());
        }

        faenaRepository.delete(foundFaena);
    }

    @Override
    @Scheduled(fixedRate = 3600000) // Cada hora
    public void automatizarCancelacionFaenasExpiradas() {
        LocalDateTime limite = LocalDateTime.now().minusHours(24); // Faenas planificadas que no iniciaron en 24 horas
        int cantidadCanceladas = faenaRepository.cancelarFaenasExpiradas(limite);
        System.out.println("Se cancelaron " + cantidadCanceladas + " faenas expiradas automáticamente.");
    }

    // --- MÉTODOS ANALÍTICOS ---

    public List<FaenaDTO> listarFaenasPorUsuarioYEstado(Long idUsuario, String estado) {
        List<Faena> faenaResumen = faenaRepository.findByUsuarioIdAndEstado(idUsuario, estado);
        List<FaenaDTO> faenaResumenDTO = new ArrayList<>();

        for (Faena f : faenaResumen) {
            FaenaDTO dto = new FaenaDTO();

            dto.setId(f.getId());
            dto.setFechaHoraSalida(f.getFechaHoraSalida());
            dto.setFechaHoraLlegada(f.getFechaHoraLlegada());
            dto.setEstado(f.getEstado());
            dto.setUsuarioId(f.getUsuario().getId());
            dto.setEmbarcacionId(f.getEmbarcacion().getId());

            faenaResumenDTO.add(dto);
        }

        return faenaResumenDTO;
    }

    @Override
    public Double obtenerTiempoPromedioFaena(Long idUsuario, LocalDateTime inicio, LocalDateTime fin) {
        Double promedio;
        if (inicio == null && fin == null){
            promedio = faenaRepository.calcularTiempoPromedioFaenaMinutos(idUsuario);
            return promedio != null ? promedio : 0.0;
        }

        if ((inicio == null || fin == null)){
            throw new BadRequestException("No se puede enviar un solo filtro de fecha. Las opciones son enviar los dos filtros " +
                    "de fecha o ninguno.");
        }

        promedio = faenaRepository.calcularTiempoPromedioFaenaMinutosEnRangoFecha(idUsuario, inicio, fin);
        return promedio != null ? promedio : 0.0;
    }
}