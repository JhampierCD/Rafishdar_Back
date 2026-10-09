package pe.edu.upc.rafishdar_back.serviceimpl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.GastoOperativoRequestDTO;
import pe.edu.upc.rafishdar_back.entities.BitacoraFaena;
import pe.edu.upc.rafishdar_back.entities.GastoOperativo;
import pe.edu.upc.rafishdar_back.exceptions.ConflictException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.repositories.BitacoraFaenaRepository;
import pe.edu.upc.rafishdar_back.repositories.GastoOperativoRepository;
import pe.edu.upc.rafishdar_back.services.GastoOperativoService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class GastoOperativoServiceImpl implements GastoOperativoService {

    @Autowired
    private GastoOperativoRepository gastoRepository;

    @Autowired
    private BitacoraFaenaRepository bitacoraRepository;

    // --- MÉTODOS BASE (CRUD) ---

    @Override
    public List<GastoOperativo> listarTodo() {
        return gastoRepository.findAll();
    }

    @Override
    public GastoOperativo insertarGasto(Long idBitacora, GastoOperativoRequestDTO request) {
        // Regla 1:1 - Verificamos si la faena ya tiene un gasto registrado
        if (gastoRepository.findByBitacoraId(idBitacora).isPresent()) {
            throw new ConflictException("Esta bitácora ya tiene gastos registrados. Utilice el método de actualización.");
        }

        BitacoraFaena bitacora = bitacoraRepository.findById(idBitacora)
                .orElseThrow(() -> new ResourceNotFoundException("Bitácora no encontrada"));

        GastoOperativo gasto = new GastoOperativo();
        gasto.setBitacora(bitacora);
        gasto.setGalonesCombustible(request.getGalonesCombustible());
        gasto.setCostoCombustible(request.getCostoCombustible());
        gasto.setCostoHieloInsumos(request.getCostoHieloInsumos());

        return gastoRepository.save(gasto);
    }

    @Override
    public GastoOperativo actualizarGasto(Long idBitacora, GastoOperativoRequestDTO request) {
        GastoOperativo gasto = gastoRepository.findByBitacoraId(idBitacora)
                .orElseThrow(() -> new ResourceNotFoundException("No existen gastos registrados para esta bitácora"));

        gasto.setGalonesCombustible(request.getGalonesCombustible());
        gasto.setCostoCombustible(request.getCostoCombustible());
        gasto.setCostoHieloInsumos(request.getCostoHieloInsumos());

        return gastoRepository.save(gasto);
    }

    @Override
    public GastoOperativo buscarGastoPorBitacora(Long idBitacora) {
        return gastoRepository.findByBitacoraId(idBitacora)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto operativo no encontrado"));
    }

    @Override
    public void eliminar(Long idBitacora) {
        GastoOperativo gasto = gastoRepository.findByBitacoraId(idBitacora)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Gasto operativo no encontrado para la bitácora"
                ));
        if ("Finalizada".equals(gasto.getBitacora().getEstado())) {
            throw new ConflictException(
                    "No se puede eliminar el gasto de una bitácora finalizada"
            );
        }
        gastoRepository.deleteByBitacoraId(idBitacora);
    }

    // --- MÉTODOS ANALÍTICOS ---

    @Override
    public Double obtenerTotalGastosPorRango(Long idUsuario, LocalDateTime inicio, LocalDateTime fin) {
        Double total = gastoRepository.calcularTotalGastosOperativos(idUsuario, inicio, fin);
        return total != null ? total : 0.0;
    }

    @Override
    public Double obtenerPromedioGastosPorRango(Long idUsuario, LocalDateTime inicio, LocalDateTime fin) {
        Double promedio = gastoRepository.calcularPromedioGastosPorFaena(idUsuario, inicio, fin);
        return promedio != null ? promedio : 0.0;
    }

    // --- MÉTODOS INNOVADORES ---

    @Override
    public Double obtenerEficienciaCombustiblePorZona(Long idZona, int diasAtras) {
        LocalDateTime fechaDesde = LocalDateTime.now().minusDays(diasAtras);
        Double indice = gastoRepository.calcularIndiceKgPorGalonEnZona(idZona, fechaDesde);
        return indice != null ? indice : 0.0;
    }
}