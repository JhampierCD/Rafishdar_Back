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

    private static final LocalDateTime FIN_DE_INTERVALO_ABIERTO =
            LocalDateTime.of(9999, 12, 31, 23, 59, 59);

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

    @Autowired
    private FaenaAuthorizationService autorizacionFaena;

    // --- MÉTODOS BASE (CRUD) ---

    @Override
    public List<Faena> listarTodo() {
        if (!autorizacionFaena.esAdministrador()) {
            Long usuarioId = autorizacionFaena.obtenerUsuarioAutenticadoId();
            return faenaRepository.findByUsuarioId(usuarioId);
        }
        return faenaRepository.findAll();
    }

    @Override
    public List<FaenaDTO> listarTodoDTO() {
        List<Faena> faenas = listarTodo();
        List<FaenaDTO> listaDTOs = new ArrayList<>();

        for (Faena faena : faenas){
            FaenaDTO faenaDTO = new FaenaDTO();

            faenaDTO.setId(faena.getId());
            faenaDTO.setFechaHoraSalidaProgramada(faena.getFechaHoraSalidaProgramada());
            faenaDTO.setFechaHoraLlegadaEstimada(faena.getFechaHoraLlegadaEstimada());
            faenaDTO.setFechaHoraSalida(faena.getFechaHoraSalida());
            faenaDTO.setFechaHoraLlegada(faena.getFechaHoraLlegada());
            faenaDTO.setEstado(faena.getEstado());
            faenaDTO.setUsuarioId(faena.getUsuario() != null ? faena.getUsuario().getId() : null);
            faenaDTO.setEmbarcacionId(faena.getEmbarcacion() != null ? faena.getEmbarcacion().getId() : null);

            listaDTOs.add(faenaDTO);
        }

        return listaDTOs;
    }

    @Override
    public List<FaenaDTO> listarFaenasPorUsuarioId(Long idUsuario) {
        if (idUsuario == null) {
            throw new BadRequestException("El ID del usuario es obligatorio");
        }
        autorizacionFaena.validarAccesoUsuario(idUsuario);

        if (!userRepository.existsById(idUsuario)){
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        List<Faena> faenas = faenaRepository.findByUsuarioId(idUsuario);
        List<FaenaDTO> listaDTOs = new ArrayList<>();

        for (Faena faena : faenas){
            FaenaDTO faenaDTO = new FaenaDTO();

            faenaDTO.setId(faena.getId());
            faenaDTO.setFechaHoraSalidaProgramada(faena.getFechaHoraSalidaProgramada());
            faenaDTO.setFechaHoraLlegadaEstimada(faena.getFechaHoraLlegadaEstimada());
            faenaDTO.setFechaHoraSalida(faena.getFechaHoraSalida());
            faenaDTO.setFechaHoraLlegada(faena.getFechaHoraLlegada());
            faenaDTO.setEstado(faena.getEstado());
            faenaDTO.setUsuarioId(faena.getUsuario() != null ? faena.getUsuario().getId() : null);
            faenaDTO.setEmbarcacionId(faena.getEmbarcacion() != null ? faena.getEmbarcacion().getId() : null);

            listaDTOs.add(faenaDTO);
        }

        return listaDTOs;
    }

    @Override
    public Faena buscarFaenaPorId(Long idFaena) {
        if (idFaena == null){
            throw new BadRequestException("El ID de la faena es obligatorio");
        }

        Faena faena = faenaRepository.findById(idFaena)
                .orElseThrow(() -> new ResourceNotFoundException("Faena no encontrada"));
        autorizacionFaena.validarAcceso(faena, false);
        return faena;
    }

    @Override
    public Faena insertarFaena(FaenaRequestDTO request) {
        if (request == null) {
            throw new BadRequestException("El request de la faena es obligatorio");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new org.springframework.security.authentication.AuthenticationCredentialsNotFoundException(
                    "Se requiere una sesión autenticada");
        }

        String userEmail = auth.getName();

        User usuario = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (request.getEmbarcacionId() == null) {
            throw new BadRequestException("El ID de la embarcación es obligatorio");
        }

        Embarcacion embarcacion = embarcacionRepository.findByIdAndUsuarioId(request.getEmbarcacionId(), usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Embarcación no encontrada o no pertenece al usuario"));

        LocalDateTime salidaProgramada = request.getFechaHoraSalidaProgramada();
        LocalDateTime llegadaEstimada = request.getFechaHoraLlegadaEstimada();

        if (salidaProgramada == null) {
            throw new BadRequestException("La fecha y hora de salida programada son obligatorias");
        }

        if (llegadaEstimada != null && !llegadaEstimada.isAfter(salidaProgramada)) {
            throw new BadRequestException("La llegada estimada debe ser posterior a la salida programada");
        }
        if (salidaProgramada.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La salida programada no puede estar en el pasado");
        }

        if (existeFaenaSolapada(
                usuario.getId(),
                embarcacion.getId(),
                salidaProgramada,
                llegadaEstimada,
                null)) {
            throw new ConflictException("El usuario o la embarcación ya tienen otra faena en ese horario");
        }

        Faena faena = new Faena();

        faena.setUsuario(usuario);
        faena.setEmbarcacion(embarcacion);

        faena.setFechaHoraSalidaProgramada(salidaProgramada);
        faena.setFechaHoraLlegadaEstimada(llegadaEstimada);

        faena.setFechaHoraSalida(null);
        faena.setFechaHoraLlegada(null);

        faena.setEstado("Planificada");

        return faenaRepository.save(faena);
    }

    @Override
    public Faena cambiarEstadoAEnCurso(Long idFaena) {
        Faena faena = buscarFaenaPorId(idFaena);
        autorizacionFaena.validarAcceso(faena, true);

        if (!"Planificada".equals(faena.getEstado())) {
            throw new ConflictException("Solo las faenas planificadas pueden pasar a 'En curso'");
        }

        faena.setFechaHoraSalida(LocalDateTime.now());
        faena.setFechaHoraLlegada(null);
        faena.setEstado("En curso");

        return faenaRepository.save(faena);
    }

    @Override
    public Faena cambiarEstadoAPlanificada(Long idFaena) {
        Faena faena = buscarFaenaPorId(idFaena);
        autorizacionFaena.validarAcceso(faena, true);

        if (!"En curso".equals(faena.getEstado())) {
            throw new ConflictException("Solo se puede revertir a 'Planificada' una faena 'En curso'.");
        }

        if (faena.getBitacoras() != null && !faena.getBitacoras().isEmpty()) {
            throw new ConflictException(
                    "No se puede revertir a 'Planificada' una faena que ya tiene bitácoras registradas.");
        }

        if (existeFaenaSolapada(
                faena.getUsuario().getId(),
                faena.getEmbarcacion().getId(),
                faena.getFechaHoraSalidaProgramada(),
                faena.getFechaHoraLlegadaEstimada(),
                faena.getId())) {
            throw new ConflictException("El usuario o la embarcación ya tienen otra faena en ese horario");
        }

        // Limpiamos las fechas reales
        faena.setFechaHoraSalida(null);
        faena.setFechaHoraLlegada(null);

        // Conservamos las fechas programadas y estimadas
        faena.setEstado("Planificada");

        return faenaRepository.save(faena);
    }

    @Override
    public Faena actualizarPlanificada(Long idFaena, FaenaUpdateDTO datosNuevos) {
        if (datosNuevos == null) {
            throw new BadRequestException("El request de actualización es obligatorio");
        }

        Faena faena = buscarFaenaPorId(idFaena);
        autorizacionFaena.validarAcceso(faena, true);

        if (!"Planificada".equals(faena.getEstado())) {
            throw new ConflictException("Este método solo permite actualizar faenas en estado 'Planificada'.");
        }
        if (datosNuevos.getFechaHoraSalidaProgramada() == null &&
                datosNuevos.getFechaHoraLlegadaEstimada() == null &&
                datosNuevos.getEmbarcacionId() == null) {
            throw new BadRequestException("Debe proporcionar al menos un campo para actualizar");
        }

        User usuario = faena.getUsuario();
        Embarcacion embarcacion = faena.getEmbarcacion();

        if (datosNuevos.getEmbarcacionId() != null) {
            embarcacion = embarcacionRepository.findByIdAndUsuarioId(datosNuevos.getEmbarcacionId(), usuario.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Embarcación no encontrada o no pertenece al usuario"));
        }

        LocalDateTime nuevaSalidaProgramada = datosNuevos.getFechaHoraSalidaProgramada() != null
                        ? datosNuevos.getFechaHoraSalidaProgramada() : faena.getFechaHoraSalidaProgramada();

        LocalDateTime nuevaLlegadaEstimada = datosNuevos.getFechaHoraLlegadaEstimada() != null
                        ? datosNuevos.getFechaHoraLlegadaEstimada() : faena.getFechaHoraLlegadaEstimada();

        if (nuevaSalidaProgramada == null) {
            throw new BadRequestException("La fecha de salida programada es obligatoria");
        }

        if (nuevaLlegadaEstimada != null &&
                !nuevaLlegadaEstimada.isAfter(nuevaSalidaProgramada)) {
            throw new BadRequestException("La llegada estimada debe ser posterior a la salida programada");
        }

        if (existeFaenaSolapada(
                usuario.getId(),
                embarcacion.getId(),
                nuevaSalidaProgramada,
                nuevaLlegadaEstimada,
                faena.getId())) {
            throw new ConflictException("El usuario o la embarcación ya tienen otra faena en ese horario");
        }

        faena.setEmbarcacion(embarcacion);
        faena.setFechaHoraSalidaProgramada(nuevaSalidaProgramada);
        faena.setFechaHoraLlegadaEstimada(nuevaLlegadaEstimada);

        return faenaRepository.save(faena);
    }

    @Override
    public Faena actualizarEnCurso(Long idFaena, FaenaUpdateDTO datosNuevos) {
        if (datosNuevos == null) {
            throw new BadRequestException("El request de actualización es obligatorio");
        }

        Faena faena = buscarFaenaPorId(idFaena);
        autorizacionFaena.validarAcceso(faena, true);

        if (!"En curso".equals(faena.getEstado())) {
            throw new ConflictException("Este método solo permite actualizar faenas en estado 'En curso'.");
        }

        if (datosNuevos.getEmbarcacionId() != null ||
                datosNuevos.getFechaHoraSalidaProgramada() != null) {
            throw new BadRequestException("En una faena en curso solo se puede modificar la llegada estimada");
        }
        if (datosNuevos.getFechaHoraLlegadaEstimada() == null) {
            throw new BadRequestException("La llegada estimada es obligatoria para esta actualización");
        }

        if (datosNuevos.getFechaHoraLlegadaEstimada() != null) {

            LocalDateTime nuevaLlegadaEstimada = datosNuevos.getFechaHoraLlegadaEstimada();

            if (faena.getFechaHoraSalida() == null ||
                    !nuevaLlegadaEstimada.isAfter(faena.getFechaHoraSalida())) {
                throw new BadRequestException(
                        "La llegada estimada debe ser posterior a la salida real");
            }

            if (existeFaenaSolapada(
                    faena.getUsuario().getId(),
                    faena.getEmbarcacion().getId(),
                    faena.getFechaHoraSalida(),
                    nuevaLlegadaEstimada,
                    faena.getId())) {
                throw new ConflictException("El usuario o la embarcación ya tienen otra faena en ese horario");
            }

            faena.setFechaHoraLlegadaEstimada(nuevaLlegadaEstimada);
        }

        return faenaRepository.save(faena);
    }

    @Override
    public Faena terminarFaena(Long idFaena, Boolean ultimaBitacora, BitacoraRequestDTO request) {
        Faena faena = buscarFaenaPorId(idFaena);
        autorizacionFaena.validarAcceso(faena, true);
        if (ultimaBitacora == null) {
            throw new BadRequestException("Debe indicar si se registra la última bitácora");
        }

        if (!"En curso".equals(faena.getEstado())) {
            throw new ConflictException("Solo las faenas 'En curso' pueden finalizarse");
        }

        if (ultimaBitacora) {
            if (request == null) {
                throw new BadRequestException("Se requiere información de la última bitácora para finalizar la faena");
            }

            if (request.getFaenaId() == null || !request.getFaenaId().equals(idFaena)) {
                throw new BadRequestException("La bitácora debe pertenecer a la faena que se está finalizando");
            }

            bitacoraService.insertarBitacora(request);
        }

        LocalDateTime llegadaReal = LocalDateTime.now();

        if (faena.getFechaHoraSalida() == null ||
                !llegadaReal.isAfter(faena.getFechaHoraSalida())) {
            throw new ConflictException("La llegada real debe ser posterior a la salida real");
        }

        faena.setFechaHoraLlegada(llegadaReal);
        faena.setEstado("Finalizada");

        return faenaRepository.save(faena);
    }

    @Override
    public Faena cancelarFaenaEnCursoOFinalizada(Long idFaena) {
        Faena foundFaena = buscarFaenaPorId(idFaena);
        autorizacionFaena.validarAcceso(foundFaena, true);
        if (!"En curso".equals(foundFaena.getEstado()) && !"Finalizada".equals(foundFaena.getEstado())) {
            throw new ConflictException("Solo las faenas 'En curso' o 'Finalizada' pueden cancelarse");
        }
        foundFaena.setEstado("Cancelada");
        return faenaRepository.save(foundFaena);
    }

    @Override
    public Boolean eliminarFaenaPlanificada(Long idFaena) {
        Faena foundFaena = buscarFaenaPorId(idFaena);
        autorizacionFaena.validarAcceso(foundFaena, true);
        if (!"Planificada".equals(foundFaena.getEstado())) {
            throw new ConflictException("No se puede eliminar una faena finalizada o en curso.");
        }
        if (foundFaena.getBitacoras() != null && !foundFaena.getBitacoras().isEmpty()) {
            throw new ConflictException("No se puede eliminar una faena que ya tiene bitácoras registradas");
        }

        if (foundFaena.getGastoOperativo() != null){
            gastoOperativoRepository.delete(foundFaena.getGastoOperativo());
        }

        if (foundFaena.getRecomendacionIA() != null){
            recomendacionIARepository.delete(foundFaena.getRecomendacionIA());
        }

        faenaRepository.delete(foundFaena);
        return true;
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
        if (idUsuario == null || estado == null || estado.isBlank()){
            throw new BadRequestException("El ID de usuario y el estado son obligatorios");
        }
        autorizacionFaena.validarAccesoUsuario(idUsuario);

        if (!userRepository.existsById(idUsuario)){
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        if (!estado.equals("Planificada") && !estado.equals("En curso")
                && !estado.equals("Finalizada") && !estado.equals("Cancelada")) {
            throw new BadRequestException("El estado proporcionado no es válido");
        }

        List<Faena> faenaResumen = faenaRepository.findByUsuarioIdAndEstado(idUsuario, estado);
        List<FaenaDTO> faenaResumenDTO = new ArrayList<>();

        for (Faena f : faenaResumen) {
            FaenaDTO dto = new FaenaDTO();

            dto.setId(f.getId());
            dto.setFechaHoraSalidaProgramada(f.getFechaHoraSalidaProgramada());
            dto.setFechaHoraLlegadaEstimada(f.getFechaHoraLlegadaEstimada());
            dto.setFechaHoraSalida(f.getFechaHoraSalida());
            dto.setFechaHoraLlegada(f.getFechaHoraLlegada());
            dto.setEstado(f.getEstado());
            dto.setUsuarioId(f.getUsuario() != null ? f.getUsuario().getId() : null);
            dto.setEmbarcacionId(f.getEmbarcacion() != null ? f.getEmbarcacion().getId() : null);

            faenaResumenDTO.add(dto);
        }

        return faenaResumenDTO;
    }

    @Override
    public Double obtenerTiempoPromedioFaena(Long idUsuario, LocalDateTime inicio, LocalDateTime fin) {
        if (idUsuario == null) {
            throw new BadRequestException("El ID de usuario es obligatorio");
        }
        autorizacionFaena.validarAccesoUsuario(idUsuario);

        if (!userRepository.existsById(idUsuario)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        if (inicio == null && fin == null) {
            Double promedio = faenaRepository.calcularTiempoPromedioFaenaMinutos(idUsuario);
            return promedio != null ? promedio : 0.0;
        }

        if (inicio == null || fin == null) {
            throw new BadRequestException(
                    "Debe enviar ambas fechas o ninguna de ellas");
        }

        if (inicio.isAfter(fin)) {
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        Double promedio = faenaRepository.calcularTiempoPromedioFaenaMinutosEnRangoFecha(
                idUsuario, inicio, fin);

        return promedio != null ? promedio : 0.0;
    }

    private boolean existeFaenaSolapada(
            Long usuarioId,
            Long embarcacionId,
            LocalDateTime salida,
            LocalDateTime llegada,
            Long faenaExcluirId) {
        return faenaRepository.existeFaenaSolapada(
                usuarioId,
                embarcacionId,
                salida,
                llegada == null ? FIN_DE_INTERVALO_ABIERTO : llegada,
                faenaExcluirId);
    }
}