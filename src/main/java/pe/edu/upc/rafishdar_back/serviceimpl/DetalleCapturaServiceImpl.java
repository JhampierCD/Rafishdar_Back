package pe.edu.upc.rafishdar_back.serviceimpl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.DetalleCapturaRequestDTO;
import pe.edu.upc.rafishdar_back.entities.BitacoraFaena;
import pe.edu.upc.rafishdar_back.entities.DetalleCaptura;
import pe.edu.upc.rafishdar_back.entities.EspeciePez;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.repositories.BitacoraFaenaRepository;
import pe.edu.upc.rafishdar_back.repositories.DetalleCapturaRepository;
import pe.edu.upc.rafishdar_back.repositories.EspeciePezRepository;
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
    private BitacoraFaenaRepository bitacoraRepository;

    @Autowired
    private EspeciePezRepository especieRepository;

    // --- MÉTODOS BASE (CRUD) ---

    @Override
    public List<DetalleCaptura> listarTodo() {
        return capturaRepository.findAll();
    }

    @Override
    public DetalleCaptura insertarCaptura(Long idBitacora, DetalleCapturaRequestDTO request) {
        if (request.getVolumenKg() <= 0) {
            throw new BadRequestException("El volumen de captura debe ser mayor a 0");
        }
        BitacoraFaena bitacora = bitacoraRepository.findById(idBitacora)
                .orElseThrow(() -> new ResourceNotFoundException("Bitácora no encontrada"));
        EspeciePez especie = especieRepository.findById(request.getIdEspecie())
                .orElseThrow(() -> new ResourceNotFoundException("Especie no encontrada"));

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
    public List<DetalleCaptura> listarCapturasPorBitacora(Long idBitacora) {
        return capturaRepository.findByBitacoraId(idBitacora);
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
        List<Object[]> resultados = capturaRepository.encontrarMejoresZonasPorEspecieYMes(idEspecie, mesActual, PageRequest.of(0, 3));

        List<Long> zonasIds = new ArrayList<>();
        for (Object[] fila : resultados) {
            // fila[0] corresponde a b.zona.id según el query del repository
            zonasIds.add((Long) fila[0]);
        }
        return zonasIds;
    }

    @Override
    public Double obtenerPorcentajeCapturaIncidental(Long idBitacora) {
        Double volumenVeda = capturaRepository.calcularVolumenCapturaEnVeda(idBitacora);
        if (volumenVeda == null || volumenVeda == 0.0) return 0.0;

        // Calculamos el volumen total de esa misma faena (reutilizando metodo o logica directa)
        List<DetalleCaptura> capturas = listarCapturasPorBitacora(idBitacora);
        Double volumenTotal = capturas.stream().mapToDouble(DetalleCaptura::getVolumenKg).sum();

        if (volumenTotal == 0.0) return 0.0;

        return (volumenVeda / volumenTotal) * 100;
    }
}
