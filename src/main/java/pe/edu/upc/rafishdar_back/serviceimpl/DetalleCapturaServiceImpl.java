package pe.edu.upc.rafishdar_back.serviceimpl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.detalle_captura.DetalleCapturaDTO;
import pe.edu.upc.rafishdar_back.dtos.detalle_captura.DetalleCapturaRequestDTO;
import pe.edu.upc.rafishdar_back.entities.*;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.exceptions.ConflictException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.repositories.*;
import pe.edu.upc.rafishdar_back.services.DetalleCapturaService;
import org.springframework.security.access.AccessDeniedException;
import pe.edu.upc.rafishdar_back.entities.Faena;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DetalleCapturaServiceImpl implements DetalleCapturaService {

    @Autowired
    private DetalleCapturaRepository capturaRepository;

    @Autowired
    private BitacoraRepository bitacoraRepository;

    @Autowired
    private EspeciePezRepository especieRepository;

    @Autowired
    private CuotaPescaRepository cuotaPescaRepository;

    @Autowired
    private TemporadaRepository temporadaRepository;

    @Autowired
    private FaenaAuthorizationService autorizacionFaena;


    // --- MÉTODOS BASE (CRUD) ---

    @Override
    public List<DetalleCaptura> listarTodo() {
        return capturaRepository.findAll();
    }

    @Override
    public List<DetalleCapturaDTO> listarTodoDTO() {
        List<DetalleCaptura> capturas = capturaRepository.findAll();
        List<DetalleCapturaDTO> capturasDTO = new ArrayList<>();

        for (DetalleCaptura captura : capturas) {
            DetalleCapturaDTO dto = new DetalleCapturaDTO();

            dto.setId(captura.getId());
            dto.setVolumenKg(captura.getVolumenKg());
            dto.setEspecieId(captura.getEspecie().getId());
            dto.setBitacoraId(captura.getBitacora().getId());
            capturasDTO.add(dto);
        }

        return capturasDTO;
    }

    @Override
    public List<DetalleCapturaDTO> listarCapturasPorBitacora(Long idBitacora) {
        validarId(idBitacora, "El ID de la bitácora es obligatorio");
        Bitacora bitacora = bitacoraRepository.findById(idBitacora)
                .orElseThrow(() -> new ResourceNotFoundException("Bitácora no encontrada"));
        autorizacionFaena.validarAcceso(bitacora.getFaena(), false);
        List<DetalleCaptura> capturas = capturaRepository.findByBitacoraId(idBitacora);
        List<DetalleCapturaDTO> capturasDTO = new ArrayList<>();

        for (DetalleCaptura captura : capturas) {
            DetalleCapturaDTO dto = new DetalleCapturaDTO();

            dto.setId(captura.getId());
            dto.setVolumenKg(captura.getVolumenKg());
            dto.setEspecieId(captura.getEspecie().getId());
            dto.setBitacoraId(captura.getBitacora().getId());
            capturasDTO.add(dto);
        }

        return capturasDTO;
    }

    @Override
    public DetalleCaptura insertarCaptura(Long bitacoraId, DetalleCapturaRequestDTO request) {
        validarId(bitacoraId, "El ID de la bitácora es obligatorio");
        if (request == null || request.getEspecieId() == null) {
            throw new BadRequestException("Los datos de captura y el ID de especie son obligatorios");
        }
        validarVolumen(request.getVolumenKg());

        Bitacora bitacora = bitacoraRepository.findById(bitacoraId)
                .orElseThrow(() -> new ResourceNotFoundException("Bitácora no encontrada"));
        autorizacionFaena.validarAcceso(bitacora.getFaena(), true);
        if (bitacora.getFechaHoraRegistro() == null || bitacora.getFaena() == null ||
                bitacora.getFaena().getUsuario() == null) {
            throw new BadRequestException("La bitácora debe estar asociada a una faena y tener fecha de registro");
        }

        EspeciePez especie = especieRepository.findById(request.getEspecieId())
                .orElseThrow(() -> new ResourceNotFoundException("Especie no encontrada"));
        Temporada temporada = temporadaRepository.findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                bitacora.getFechaHoraRegistro().toLocalDate(),
                bitacora.getFechaHoraRegistro().toLocalDate()
        );

        if (temporada == null){
            throw new ResourceNotFoundException("Temporada no encontrada");
        }

        if (!validarCuota(bitacora.getFaena().getUsuario().getId(), especie.getId(),
                request.getVolumenKg(), temporada, null)) {
            throw new ConflictException("El volumen de captura excede la cuota permitida para esta especie en la temporada");
        }

        DetalleCaptura detalle = new DetalleCaptura();
        detalle.setBitacora(bitacora);
        detalle.setEspecie(especie);
        detalle.setVolumenKg(request.getVolumenKg());

        return capturaRepository.save(detalle);
    }

    @Override
    public DetalleCaptura actualizarCaptura(Long idDetalle, Double nuevoVolumen) {
        validarId(idDetalle, "El ID del detalle de captura es obligatorio");
        validarVolumen(nuevoVolumen);

        DetalleCaptura detalle = capturaRepository.findById(idDetalle)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de captura no encontrado"));
        autorizacionFaena.validarAcceso(detalle.getBitacora().getFaena(), true);
        if (detalle.getBitacora() == null || detalle.getBitacora().getFaena() == null ||
                detalle.getBitacora().getFaena().getUsuario() == null ||
                detalle.getBitacora().getFechaHoraRegistro() == null) {
            throw new BadRequestException("El detalle de captura no tiene una relación válida con faena");
        }

        Temporada temporada = temporadaRepository.findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                detalle.getBitacora().getFechaHoraRegistro().toLocalDate(),
                detalle.getBitacora().getFechaHoraRegistro().toLocalDate()
        );

        if (temporada == null){
            throw new ResourceNotFoundException("Temporada no encontrada");
        }

        if (!validarCuota(detalle.getBitacora().getFaena().getUsuario().getId(),
                detalle.getEspecie().getId(), nuevoVolumen, temporada, detalle.getId())) {
            throw new ConflictException("El volumen de captura excede la cuota permitida para esta especie en la temporada");
        }

        detalle.setVolumenKg(nuevoVolumen);
        return capturaRepository.save(detalle);
    }

    @Override
    public void eliminarCaptura(Long idDetalle) {
        validarId(idDetalle, "El ID del detalle de captura es obligatorio");
        DetalleCaptura detalle = capturaRepository.findById(idDetalle)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de captura no encontrado"));
        autorizacionFaena.validarAcceso(detalle.getBitacora().getFaena(), true);
        capturaRepository.delete(detalle);
    }

    @Override
    public Boolean validarVolumenMenorACuota(Long usuarioId, Long especieId, Double volumenActual, Temporada temporada) {
        return validarCuota(usuarioId, especieId, volumenActual, temporada, null);
    }

    private boolean validarCuota(Long usuarioId, Long especieId, Double volumenActual,
                                  Temporada temporada, Long detalleExcluirId) {
        if (usuarioId == null || especieId == null || temporada == null ||
                temporada.getFechaInicio() == null || temporada.getFechaFin() == null) {
            throw new BadRequestException("El usuario, la especie y la temporada son obligatorios");
        }
        validarVolumen(volumenActual);

        Double volumenHistoricoKg = capturaRepository.calcularVolumenHistoricoKg(usuarioId, especieId,
                temporada.getFechaInicio().atStartOfDay(), temporada.getFechaFin().plusDays(1).atStartOfDay(),
                detalleExcluirId);

        Double volumenTotalProyectado = volumenHistoricoKg + volumenActual;

        CuotaPesca cuotaPesca = cuotaPescaRepository.findByEspecieIdAndTemporadaId(especieId, temporada.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuota de pesca no encontrada para la especie y temporada especificadas"));

        Double limiteKg = cuotaPesca.getLimiteToneladasIndustrial() * 1000.0; // Convertir toneladas a kilogramos

        return volumenTotalProyectado <= limiteKg;
    }

    // --- MÉTODOS ANALÍTICOS ---

    @Override
    public Double obtenerVolumenTotalPorRango(Long idUsuario, LocalDateTime inicio, LocalDateTime fin) {
        validarRango(idUsuario, inicio, fin);
        autorizacionFaena.validarAccesoUsuario(idUsuario);
        Double total = capturaRepository.calcularVolumenTotal(idUsuario, inicio, fin);
        return total != null ? total : 0.0;
    }

    @Override
    public Double obtenerVolumenTotalPorEspecieYRango(Long idUsuario, Long idEspecie, LocalDateTime inicio, LocalDateTime fin) {
        validarRango(idUsuario, inicio, fin);
        if (idEspecie == null) {
            throw new BadRequestException("El ID de especie es obligatorio");
        }
        autorizacionFaena.validarAccesoUsuario(idUsuario);
        Double total = capturaRepository.calcularVolumenPorEspecie(idUsuario, idEspecie, inicio, fin);
        return total != null ? total : 0.0;
    }

    // --- MÉTODOS INNOVADORES ---

    @Override
    public List<Long> obtenerTopZonasParaEspecieEnMesCorriente(Long idEspecie) {
        if (idEspecie == null) {
            throw new BadRequestException("El ID de especie es obligatorio");
        }
        int mesActual = LocalDateTime.now().getMonthValue();
        int anioActual = LocalDateTime.now().getYear();
        // Top 3 de zonas usando PageRequest
        List<Object[]> resultados = capturaRepository.encontrarMejoresZonasPorEspecieYMes(
                idEspecie, mesActual, anioActual, PageRequest.of(0, 3));

        List<Long> zonasIds = new ArrayList<>();
        for (Object[] fila : resultados) {
            zonasIds.add(((Number) fila[0]).longValue());
        }
        return zonasIds;
    }

    @Override
    public Double obtenerPorcentajeCapturaIncidental(Long idBitacora) {
        validarId(idBitacora, "El ID de la bitácora es obligatorio");
        Bitacora bitacora = bitacoraRepository.findById(idBitacora)
                .orElseThrow(() -> new ResourceNotFoundException("Bitácora no encontrada"));
        autorizacionFaena.validarAcceso(bitacora.getFaena(), false);
        Double volumenVeda = capturaRepository.calcularVolumenCapturaEnVeda(idBitacora);
        if (volumenVeda == null || volumenVeda == 0.0) return 0.0;

        // Calculamos el volumen total de esa misma faena (reutilizando metodo o logica directa)
        List<DetalleCapturaDTO> capturas = listarCapturasPorBitacora(idBitacora);
        Double volumenTotal = capturas.stream().mapToDouble(DetalleCapturaDTO::getVolumenKg).sum();

        if (volumenTotal == 0.0) return 0.0;

        return (volumenVeda / volumenTotal) * 100;
    }

    private void validarId(Long id, String mensaje) {
        if (id == null) {
            throw new BadRequestException(mensaje);
        }
    }

    private void validarVolumen(Double volumen) {
        if (volumen == null || !Double.isFinite(volumen) || volumen <= 0) {
            throw new BadRequestException("El volumen de captura debe ser positivo y finito");
        }
    }

    private void validarRango(Long usuarioId, LocalDateTime inicio, LocalDateTime fin) {
        if (usuarioId == null || inicio == null || fin == null || inicio.isAfter(fin)) {
            throw new BadRequestException("El usuario y un rango de fechas válido son obligatorios");
        }
    }
}
