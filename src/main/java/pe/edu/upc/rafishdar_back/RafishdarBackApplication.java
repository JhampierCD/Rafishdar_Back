package pe.edu.upc.rafishdar_back;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import pe.edu.upc.rafishdar_back.dtos.*;
import pe.edu.upc.rafishdar_back.repositories.UserRepository;
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

    @Bean
    public CommandLineRunner startConfiguration(
            CondicionClimaticaService condicicionService,
            CuotaPescaService cuotaPescaService,
            EmbarcacionService embarcacionService,
            EspeciePezService especiePezService,
            RecomendacionIAService recomendacionIAService,
            TemporadaService temporadaService,
            UserService userService,
            ZonaPescaService zonaPescaService,
            BitacoraFaenaService bitacoraService,
            DetalleCapturaService detalleCapturaService,
            GastoOperativoService gastoService
    ){
        return args -> {
            // 1. Crear Usuario
            UserRegistroDTO user = new UserRegistroDTO();
            user.setNombres("Carlos");
            user.setApellidos("Mendoza");
            user.setCorreo("carlos@pesca.com");
            user.setPassword("123456");
            UserDTO userGuardado = userService.registrar(user);

            // 2. Crear Embarcación asignada al Usuario
            Embarcacion emb = new Embarcacion();
            emb.setNombre("La Niña II");
            emb.setMatricula("CE-12345-PM");
            emb.setCapacidadToneladas(15.5);
            emb.setUsuario(userService.buscarPorId(userGuardado.getId())); // Asignar el usuario creado
            Embarcacion embGuardada = embarcacionService.insertar(emb);

            // 3. Crear Zona de Pesca
            ZonaPesca zona = new ZonaPesca();
            zona.setNombreZona("Zona Centro - Callao");
            zona.setLatitud(-12.0500);
            zona.setLongitud(-77.2000);
            zona.setDistanciaCostaKm(8.0);
            zona.setRadioAreaKm(20.0);
            ZonaPesca zonaGuardada = zonaPescaService.insertar(zona);

            // 4. Crear Especie Marina
            EspeciePezDTO especie = new EspeciePezDTO();
            especie.setNombreComun("Anchoveta");
            especie.setEstadoVeda(false);
            EspeciePezDTO especieGuardada = especiePezService.insertar(especie);

            // 5. Crear Bitácora de Faena inicial (En curso para poder probar coordenadas y gastos)
            BitacoraFaena bitacora = new BitacoraFaena();
            bitacora.setUsuario(userService.buscarPorId(userGuardado.getId()));
            bitacora.setEmbarcacion(embGuardada);
            bitacora.setZonaPesca(zonaGuardada);
            bitacora.setEstado("En curso");
            bitacora.setFechaHoraSalida(LocalDateTime.of(2026, 10, 5, 4, 30));
            bitacora.setLatitudPesca(-12.0800);
            bitacora.setLongitudPesca(-77.2200);
            bitacora.setObservaciones("Faena de prueba con navegación en marcha");
            BitacoraFaena bitacoraGuardada = bitacoraService.insertarBitacora(bitacora);

            // 6. Crear Detalle de Captura usando el DTO
            DetalleCapturaRequestDTO capturaDTO = new DetalleCapturaRequestDTO();
            capturaDTO.setIdEspecie(especieGuardada.getId());
            capturaDTO.setVolumenKg(450.0); // Ajustado al campo que lee tu metodo
            // Si tu DTO tiene los otros campos (tallas, incidental), agrégalos aquí también.

            detalleCapturaService.insertarCaptura(bitacoraGuardada.getId(), capturaDTO);

            // 7. Crear Gasto Operativo usando el DTO
            GastoOperativoRequestDTO gastoDTO = new GastoOperativoRequestDTO();
            gastoDTO.setGalonesCombustible(35.0);
            gastoDTO.setCostoCombustible(560.0);
            gastoDTO.setCostoHieloInsumos(110.0);

            gastoService.insertarGasto(bitacoraGuardada.getId(), gastoDTO);
        };
    }
}
