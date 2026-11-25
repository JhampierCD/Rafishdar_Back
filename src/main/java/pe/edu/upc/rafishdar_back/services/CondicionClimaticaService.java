package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.condicion_climatica.CondicionClimaticaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.condicion_climatica.CondicionClimaticaUpdateDTO;
import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;

import java.util.List;
import java.util.Optional;

public interface CondicionClimaticaService {
    List<CondicionClimatica> listarTodoCondiciones();
    CondicionClimatica insertarCondicionesPorBitacora(Long idBitacora, CondicionClimaticaRequestDTO request);
    CondicionClimatica actualizarCondiciones(Long id, CondicionClimaticaUpdateDTO request);
    CondicionClimatica buscarPorId(Long id);
    Boolean eliminarCondiciones(Long id);
    // --- Métodos adicionales ---

    // US-12, BN-12: consulta condición validando propietario
    // Devuelve Optional vacío si la bitácora no pertenece al usuario
    Optional<CondicionClimatica> consultarPorBitacoraConPermisoUsuario(
            Long bitacoraId, Long usuarioId
    );

    // US-12: devuelve condición de una bitácora sin validación de propietario
    // Usado por roles Admin/Técnico
    Optional<CondicionClimatica> consultarPorBitacora(Long bitacoraId);

    // US-11, BN-11: verificar si hay condición guardada para una bitácora
    boolean existeCondicionParaBitacora(Long bitacoraId);

    // US-05 / US-34: promedio de temperatura histórica de una zona
    Double obtenerPromedioTemperaturaPorZona(Long zonaId);

    // Alerta: identifica condiciones con viento peligroso (para notificaciones US-11)
    List<CondicionClimatica> listarCondicionesPeligrosas(Double umbralVientoNudos);
}