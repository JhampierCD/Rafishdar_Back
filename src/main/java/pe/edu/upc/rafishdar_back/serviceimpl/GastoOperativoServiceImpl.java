package pe.edu.upc.rafishdar_back.serviceimpl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.dtos.GastoOperativoRequestDTO;
import pe.edu.upc.rafishdar_back.entities.Faena;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.entities.GastoOperativo;
import pe.edu.upc.rafishdar_back.exceptions.ConflictException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.repositories.FaenaRepository;
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
    private FaenaRepository faenaRepository;

    @Autowired
    private FaenaAuthorizationService autorizacionFaena;

    // --- MÉTODOS BASE (CRUD) ---

    @Override
    public List<GastoOperativo> listarTodo() {
        return gastoRepository.findAll();
    }

    @Override
    public GastoOperativo insertarGasto(Long idBitacora, GastoOperativoRequestDTO request) {
        validarIdFaena(idBitacora);
        validarRequest(request);
        Faena faena = faenaRepository.findById(idBitacora)
                .orElseThrow(() -> new ResourceNotFoundException("Faena no encontrada"));
        autorizacionFaena.validarAcceso(faena, true);

        if (gastoRepository.findByFaenaId(idBitacora).isPresent()) {
            throw new ConflictException("Esta faena ya tiene un gasto registrado. Utilice el método de actualización.");
        }

        GastoOperativo gasto = new GastoOperativo();
        gasto.setFaena(faena);
        gasto.setGalonesCombustible(request.getGalonesCombustible());
        gasto.setCostoCombustible(request.getCostoCombustible());
        gasto.setCostoHieloInsumos(request.getCostoHieloInsumos());

        return gastoRepository.save(gasto);
    }

    @Override
    public GastoOperativo actualizarGasto(Long idBitacora, GastoOperativoRequestDTO request) {
        validarIdFaena(idBitacora);
        validarRequest(request);
        GastoOperativo gasto = gastoRepository.findByFaenaId(idBitacora)
                .orElseThrow(() -> new ResourceNotFoundException("No existen gastos registrados para esta faena"));
        autorizacionFaena.validarAcceso(gasto.getFaena(), true);

        gasto.setGalonesCombustible(request.getGalonesCombustible());
        gasto.setCostoCombustible(request.getCostoCombustible());
        gasto.setCostoHieloInsumos(request.getCostoHieloInsumos());

        return gastoRepository.save(gasto);
    }

    @Override
    public GastoOperativo buscarGastoPorBitacora(Long idBitacora) {
        validarIdFaena(idBitacora);
        GastoOperativo gasto = gastoRepository.findByFaenaId(idBitacora)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto operativo no encontrado"));
        autorizacionFaena.validarAcceso(gasto.getFaena(), false);
        return gasto;
    }

    @Override
    public void eliminar(Long idBitacora) {
        validarIdFaena(idBitacora);
        GastoOperativo gasto = gastoRepository.findByFaenaId(idBitacora)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Gasto operativo no encontrado para la faena"
                ));
        autorizacionFaena.validarAcceso(gasto.getFaena(), true);
        if ("Finalizada".equalsIgnoreCase(gasto.getFaena().getEstado())) {
            throw new ConflictException(
                    "No se puede eliminar el gasto de una faena finalizada"
            );
        }
        gastoRepository.delete(gasto);
    }

    // --- MÉTODOS ANALÍTICOS ---

    @Override
    public Double obtenerTotalGastosPorRango(Long idUsuario, LocalDateTime inicio, LocalDateTime fin) {
        validarRango(idUsuario, inicio, fin);
        autorizacionFaena.validarAccesoUsuario(idUsuario);
        Double total = gastoRepository.calcularTotalGastosOperativos(idUsuario, inicio, fin);
        return total != null ? total : 0.0;
    }

    @Override
    public Double obtenerPromedioGastosPorRango(Long idUsuario, LocalDateTime inicio, LocalDateTime fin) {
        validarRango(idUsuario, inicio, fin);
        autorizacionFaena.validarAccesoUsuario(idUsuario);
        Double promedio = gastoRepository.calcularPromedioGastosPorFaena(idUsuario, inicio, fin);
        return promedio != null ? promedio : 0.0;
    }

    // --- MÉTODOS INNOVADORES ---

    @Override
    public Double obtenerEficienciaCombustiblePorZona(Long idZona, int diasAtras) {
        if (idZona == null || diasAtras <= 0) {
            throw new BadRequestException("La zona y los días deben ser válidos");
        }
        if (!Double.isFinite(diasAtras)) {
            throw new BadRequestException("Los días deben ser un número finito");
        }
        LocalDateTime fechaDesde = LocalDateTime.now().minusDays(diasAtras);
        Double indice = gastoRepository.calcularIndiceKgPorGalonEnZona(idZona, fechaDesde);
        return indice != null ? indice : 0.0;
    }

    private void validarIdFaena(Long faenaId) {
        if (faenaId == null) {
            throw new BadRequestException("El ID de la faena es obligatorio");
        }
    }

    private void validarRango(Long usuarioId, LocalDateTime inicio, LocalDateTime fin) {
        if (usuarioId == null || inicio == null || fin == null || inicio.isAfter(fin)) {
            throw new BadRequestException("El usuario y un rango de fechas válido son obligatorios");
        }
    }

    private void validarRequest(GastoOperativoRequestDTO request) {
        if (request == null) {
            throw new BadRequestException("Los datos del gasto operativo son obligatorios");
        }
        Double gallons = request.getGalonesCombustible();
        Double fuelCost = request.getCostoCombustible();
        Double suppliesCost = request.getCostoHieloInsumos();
        if (gallons == null || fuelCost == null || suppliesCost == null ||
                !Double.isFinite(gallons) || !Double.isFinite(fuelCost) ||
                !Double.isFinite(suppliesCost) || gallons <= 0 ||
                fuelCost < 0 || suppliesCost < 0) {
            throw new BadRequestException("Combustible debe ser positivo y los costos no negativos y finitos");
        }
    }
}