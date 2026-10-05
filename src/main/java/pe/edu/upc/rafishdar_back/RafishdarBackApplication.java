package pe.edu.upc.rafishdar_back;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import pe.edu.upc.rafishdar_back.dtos.DetalleCapturaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.GastoOperativoRequestDTO;
import pe.edu.upc.rafishdar_back.services.*;
import pe.edu.upc.rafishdar_back.entities.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EnableScheduling
@SpringBootApplication
public class RafishdarBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(RafishdarBackApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner startConfiguration(
//            AuditoriaService auditoriaService,
//            CondicionClimaticaService condicicionService,
//            CuotaPescaService cuotaPescaService,
//            EmbarcacionService embarcacionService,
//            EspeciePezService especiePezService,
//            RecomendacionIAService recomendacionIAService,
//            TemporadaService temporadaService,
//            UserService userService,
//            ZonaPescaService zonaPescaService,
//            BitacoraFaenaService bitacoraService,
//            DetalleCapturaService detalleCapturaService,
//            GastoOperativoService gastoService
//    ){
//        return args -> {
//            // 5. Crear Bitácora de Faena inicial (En curso para poder probar coordenadas y gastos)
//            BitacoraFaena bitacora = new BitacoraFaena();
//            bitacora.setUsuario(userGuardado);
//            bitacora.setEmbarcacion(embGuardada);
//            bitacora.setZonaPesca(zonaGuardada);
//            bitacora.setEstado("En curso");
//            bitacora.setFechaHoraSalida(LocalDateTime.of(2026, 10, 5, 4, 30));
//            bitacora.setLatitudPesca(-12.0800);
//            bitacora.setLongitudPesca(-77.2200);
//            bitacora.setObservaciones("Faena de prueba con navegación en marcha");
//            BitacoraFaena bitacoraGuardada = bitacoraService.insertarBitacora(bitacora);
//
//            // 6. Crear Detalle de Captura usando el DTO
//            DetalleCapturaRequestDTO capturaDTO = new DetalleCapturaRequestDTO();
//            capturaDTO.setIdEspecie(especieGuardada.getId());
//            capturaDTO.setVolumenKg(450.0); // Ajustado al campo que lee tu método
//            // Si tu DTO tiene los otros campos (tallas, incidental), agrégalos aquí también.
//
//            detalleCapturaService.insertarCaptura(bitacoraGuardada.getId(), capturaDTO);
//
//            // 7. Crear Gasto Operativo usando el DTO
//            GastoOperativoRequestDTO gastoDTO = new GastoOperativoRequestDTO();
//            gastoDTO.setGalonesCombustible(35.0);
//            gastoDTO.setCostoCombustible(560.0);
//            gastoDTO.setCostoHieloInsumos(110.0);
//
//            gastoService.insertarGasto(bitacoraGuardada.getId(), gastoDTO);
//        };
//    }
}
