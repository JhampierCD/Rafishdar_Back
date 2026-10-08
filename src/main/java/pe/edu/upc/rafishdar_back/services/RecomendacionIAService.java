package pe.edu.upc.rafishdar_back.services;

import pe.edu.upc.rafishdar_back.entities.RecomendacionIA;

import java.util.List;
import java.util.Optional;

public interface RecomendacionIAService {
    List<RecomendacionIA> listarTodoRecomendaciones();
    RecomendacionIA insertarRecomendaciones(RecomendacionIA recomendacionIA);
    RecomendacionIA actualizarRecomendaciones(RecomendacionIA recomendacionIA);
    RecomendacionIA buscarPorId(Long id);
    Boolean eliminarRecomendaciones(Long id);

    // --- Métodos adicionales ---

    // US-24, BN-23: genera y guarda la recomendación para una bitácora
    // Si ya existe, lanza excepción (debe usarse regenerar)
    RecomendacionIA generarParaBitacora(Long bitacoraId, Long usuarioId);

    // US-26, BN-23: regenera la recomendación actualizando el registro 1:1
    // Si NO existe, crea uno nuevo (equivale a la primera generación)
    RecomendacionIA regenerarParaBitacora(Long bitacoraId, Long usuarioId);

    // US-25, BN-24: consulta recomendación validando propietario
    Optional<RecomendacionIA> consultarPorBitacoraConPermisoUsuario(
            Long bitacoraId, Long usuarioId
    );

    // US-25: consulta sin validación de propietario (Admin / Técnico)
    Optional<RecomendacionIA> consultarPorBitacora(Long bitacoraId);

    // BN-23: verifica si existe recomendación para una bitácora
    boolean existeRecomendacionParaBitacora(Long bitacoraId);

    // US-34 Dashboard: últimas recomendaciones de un usuario
    List<RecomendacionIA> listarUltimasPorUsuario(Long usuarioId);

    // US-33 Técnico: recomendaciones con nivel ALTO en las últimas N horas
    List<RecomendacionIA> listarAltoRiesgoRecientes(int horas);

    // US-34: conteo por nivel de riesgo para indicadores del dashboard
    long contarPorNivelRiesgoYUsuario(String nivelRiesgo, Long usuarioId);
}
