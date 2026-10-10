package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.dtos.CondicionClimaticaDTO;
import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;

import java.util.List;
import java.util.Optional;

public interface CondicionClimaticaService {
    List<CondicionClimatica> listarTodoCondiciones();
    CondicionClimatica insertarCondicionesPorBitacora(Long idBitacora, CondicionClimaticaDTO dto);
    CondicionClimatica actualizarCondiciones(CondicionClimatica condicionClimatica);
    CondicionClimatica buscarPorId(Long id);
    Boolean eliminarCondiciones(Long id);
    // --- Métodos adicionales ---

    // BN-10: upsert — inserta si no existe, actualiza si existe
    // Llamado desde BitacoraService al iniciar la faena (US-03, US-11)
    CondicionClimatica guardarOActualizar(CondicionClimatica condicion);

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
