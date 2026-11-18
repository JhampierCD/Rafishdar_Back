package pe.edu.upc.rafishdar_back.serviceimpl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.detalle_captura.DetalleCapturaDTO;
import pe.edu.upc.rafishdar_back.dtos.detalle_captura.DetalleCapturaRequestDTO;
import pe.edu.upc.rafishdar_back.entities.*;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.repositories.*;
import pe.edu.upc.rafishdar_back.services.DetalleCapturaService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        if (request.getVolumenKg() <= 0) {
            throw new BadRequestException("El volumen de captura debe ser mayor a 0");
        }

        Bitacora bitacora = bitacoraRepository.findById(bitacoraId)
                .orElseThrow(() -> new ResourceNotFoundException("Bitácora no encontrada"));
        EspeciePez especie = especieRepository.findById(request.getEspecieId())
                .orElseThrow(() -> new ResourceNotFoundException("Especie no encontrada"));
        Temporada temporada = temporadaRepository.findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                bitacora.getFechaHoraRegistro().toLocalDate(),
                bitacora.getFechaHoraRegistro().toLocalDate()
        );

        if (temporada == null){
            throw new ResourceNotFoundException("Temporada no encontrada");
        }

        if (!validarVolumenMenorACuota(bitacora.getFaena().getUsuario().getId(),
                especie.getId(), request.getVolumenKg(), temporada)) {
            throw new BadRequestException("El volumen de captura excede la cuota permitida para esta especie en la temporada actual");
        }

        DetalleCaptura detalle = new DetalleCaptura();
        detalle.setBitacora(bitacora);
        detalle.setEspecie(especie);
        detalle.setVolumenKg(request.getVolumenKg());

        return capturaRepository.save(detalle);
    }

    @Override
    public DetalleCaptura actualizarCaptura(Long idDetalle, Double nuevoVolumen) {
        if (nuevoVolumen <= 0) {
            throw new BadRequestException("El volumen debe ser mayor a 0");
        }

        DetalleCaptura detalle = capturaRepository.findById(idDetalle)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de captura no encontrado"));

        Temporada temporada = temporadaRepository.findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                detalle.getBitacora().getFechaHoraRegistro().toLocalDate(),
                detalle.getBitacora().getFechaHoraRegistro().toLocalDate()
        );

        if (temporada == null){
            throw new ResourceNotFoundException("Temporada no encontrada");
        }

        if (!validarVolumenMenorACuota(detalle.getBitacora().getFaena().getUsuario().getId(),
                detalle.getEspecie().getId(), nuevoVolumen, temporada)) {
            throw new BadRequestException("El volumen de captura excede la cuota permitida para esta especie en la temporada actual");
        }

        detalle.setVolumenKg(nuevoVolumen);
        return capturaRepository.save(detalle);
    }

    @Override
    public void eliminarCaptura(Long idDetalle) {
        if (!capturaRepository.existsById(idDetalle)) {
            throw new ResourceNotFoundException("Detalle no encontrado");
        }
        capturaRepository.deleteById(idDetalle);
    }

    @Override
    public Boolean validarVolumenMenorACuota(Long usuarioId, Long especieId, Double volumenActual, Temporada temporada) {
        Double volumenHistoricoKg = capturaRepository.calcularVolumenHistoricoKg(usuarioId, especieId,
                temporada.getFechaInicio().atTime(0,0,0), temporada.getFechaFin().atTime(23,59,59));

        Double volumenTotalProyectado = volumenHistoricoKg + volumenActual;

        CuotaPesca cuotaPesca = cuotaPescaRepository.findByEspecieIdAndTemporadaId(especieId, temporada.getId());

        Double limiteKg = cuotaPesca.getLimiteToneladasIndustrial() * 1000.0; // Convertir toneladas a kilogramos

        return volumenTotalProyectado <= limiteKg;
    }

    // --- MÉTODOS ANALÍTICOS ---

    @Override
    public Double obtenerVolumenTotalPorRango(Long idUsuario, LocalDateTime inicio, LocalDateTime fin) {
        Double total = capturaRepository.calcularVolumenTotal(idUsuario, inicio, fin);
        return total != null ? total : 0.0;
    }

    @Override
    public Double obtenerVolumenTotalPorEspecieYRango(Long idUsuario, Long idEspecie, LocalDateTime inicio, LocalDateTime fin) {
        Double total = capturaRepository.calcularVolumenPorEspecie(idUsuario, idEspecie, inicio, fin);
        return total != null ? total : 0.0;
    }

    // --- MÉTODOS INNOVADORES ---

    @Override
    public List<Long> obtenerTopZonasParaEspecieEnMesCorriente(Long idEspecie) {
        int mesActual = LocalDateTime.now().getMonthValue();
        // Top 3 de zonas usando PageRequest
        List<DetalleCapturaDTO> resultados = capturaRepository.encontrarMejoresZonasPorEspecieYMes(idEspecie, mesActual, PageRequest.of(0, 3));

        List<Long> zonasIds = new ArrayList<>();
        for (DetalleCapturaDTO fila : resultados) {
            zonasIds.add(fila.getId());
        }
        return zonasIds;
    }

    @Override
    public Double obtenerPorcentajeCapturaIncidental(Long idBitacora) {
        Double volumenVeda = capturaRepository.calcularVolumenCapturaEnVeda(idBitacora);
        if (volumenVeda == null || volumenVeda == 0.0) return 0.0;

        // Calculamos el volumen total de esa misma faena (reutilizando metodo o logica directa)
        List<DetalleCapturaDTO> capturas = listarCapturasPorBitacora(idBitacora);
        Double volumenTotal = capturas.stream().mapToDouble(DetalleCapturaDTO::getVolumenKg).sum();

        if (volumenTotal == 0.0) return 0.0;

        return (volumenVeda / volumenTotal) * 100;
    }
}
