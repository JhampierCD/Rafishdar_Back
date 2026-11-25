package pe.edu.upc.rafishdar_back;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.scheduling.annotation.EnableScheduling;
import pe.edu.upc.rafishdar_back.dtos.cuota_pesca.CuotaPescaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.detalle_captura.DetalleCapturaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.bitacora.BitacoraRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.faena.FaenaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.gasto_operativo.GastoOperativoRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.user.UserDTO;
import pe.edu.upc.rafishdar_back.dtos.user.UserRegistroDTO;
import pe.edu.upc.rafishdar_back.dtos.embarcacion.EmbarcacionRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.especie_pez.EspeciePezRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.temporada.TemporadaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.zona_pesca.ZonaPescaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.condicion_climatica.CondicionClimaticaRequestDTO;
import pe.edu.upc.rafishdar_back.entities.Authority;
import pe.edu.upc.rafishdar_back.entities.Bitacora;
import pe.edu.upc.rafishdar_back.entities.Embarcacion;
import pe.edu.upc.rafishdar_back.entities.Faena;
import pe.edu.upc.rafishdar_back.entities.RecomendacionIA;
import pe.edu.upc.rafishdar_back.entities.EspeciePez;
import pe.edu.upc.rafishdar_back.entities.Temporada;
import pe.edu.upc.rafishdar_back.entities.User;
import pe.edu.upc.rafishdar_back.entities.ZonaPesca;
import pe.edu.upc.rafishdar_back.repositories.BitacoraRepository;
import pe.edu.upc.rafishdar_back.repositories.AuthorityRepository;
import pe.edu.upc.rafishdar_back.repositories.UserRepository;
import pe.edu.upc.rafishdar_back.services.BitacoraService;
import pe.edu.upc.rafishdar_back.services.CondicionClimaticaService;
import pe.edu.upc.rafishdar_back.services.CuotaPescaService;
import pe.edu.upc.rafishdar_back.services.DetalleCapturaService;
import pe.edu.upc.rafishdar_back.services.EmbarcacionService;
import pe.edu.upc.rafishdar_back.services.EspeciePezService;
import pe.edu.upc.rafishdar_back.services.FaenaService;
import pe.edu.upc.rafishdar_back.services.GastoOperativoService;
import pe.edu.upc.rafishdar_back.services.RecomendacionIAService;
import pe.edu.upc.rafishdar_back.services.TemporadaService;
import pe.edu.upc.rafishdar_back.services.UserService;
import pe.edu.upc.rafishdar_back.services.ZonaPescaService;
import pe.edu.upc.rafishdar_back.security.UserSecurity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@EnableScheduling
@SpringBootApplication
public class RafishdarBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(RafishdarBackApplication.class, args);
    }

    @Bean
    @Order(2)
    public CommandLineRunner startConfiguration(
            AuthorityRepository authorityRepository,
            BitacoraRepository bitacoraRepository,
            BitacoraService bitacoraService,
            FaenaService faenaService,
            CondicionClimaticaService condicionService,
            CuotaPescaService cuotaPescaService,
            DetalleCapturaService detalleCapturaService,
            EmbarcacionService embarcacionService,
            EspeciePezService especiePezService,
            GastoOperativoService gastoService,
            RecomendacionIAService recomendacionIAService,
            TemporadaService temporadaService,
            UserRepository userRepository,
            UserService userService,
            ZonaPescaService zonaPescaService
    ) {
        return args -> {
            if (userRepository.existsByEmailIgnoreCase("admin@rafishdar.com")) {
                return;
            }

            Authority adminRole = obtenerAuthority(authorityRepository, "ADMIN");
            Authority fishermanRole = obtenerAuthority(authorityRepository, "PESCADOR");
            Authority technicianRole = obtenerAuthority(authorityRepository, "TECNICO");

            User admin = registrarUser(
                    userService, "Ana", "Administradora",
                    "admin@rafishdar.com", "Admin123"
            );
            User fisherman = registrarUser(
                    userService, "Carlos", "Mendoza",
                    "carlos@pesca.com", "Pesca123"
            );
            User secondFisherman = registrarUser(
                    userService, "María", "Rojas",
                    "maria@pesca.com", "Pesca123"
            );
            User technician = registrarUser(
                    userService, "Luis", "Técnico",
                    "tecnico@rafishdar.com", "Tecnico123"
            );
            User inactiveUser = registrarUser(
                    userService, "Rosa", "Inactiva",
                    "inactiva@pesca.com", "Pesca123"
            );

            asignarRoles(userRepository, admin, adminRole);
            asignarRoles(userRepository, fisherman, fishermanRole);
            asignarRoles(userRepository, secondFisherman, fishermanRole);
            asignarRoles(userRepository, technician, technicianRole);
            asignarRoles(userRepository, inactiveUser, fishermanRole);
            userService.eliminarLogico(inactiveUser.getId());
            UserSecurity adminSecurity = new UserSecurity(admin);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            adminSecurity, null, adminSecurity.getAuthorities()));

            Embarcacion boatOne = crearEmbarcacion(
                    embarcacionService, "La Niña II", "CE-12345-PM",
                    15.5, fisherman
            );
            Embarcacion boatTwo = crearEmbarcacion(
                    embarcacionService, "Estrella del Sur", "CE-54321-PM",
                    22.0, fisherman
            );
            Embarcacion boatThree = crearEmbarcacion(
                    embarcacionService, "Mar Azul", "PA-98765-PM",
                    9.0, secondFisherman
            );

            ZonaPesca callao = crearZonaPesca(
                    zonaPescaService, "Zona Centro - Callao",
                    -12.0500, -77.2000, 8.0, 20.0
            );
            ZonaPesca huacho = crearZonaPesca(
                    zonaPescaService, "Zona Norte - Huacho",
                    -11.1000, -77.6000, 3.5, 14.0
            );
            ZonaPesca pisco = crearZonaPesca(
                    zonaPescaService, "Zona Sur - Pisco",
                    -13.7000, -76.3000, 16.0, 28.0
            );
            crearZonaPesca(
                    zonaPescaService, "Zona Exterior - Ilo",
                    -17.6500, -71.3500, 42.0, 35.0
            );

            EspeciePez anchoveta = crearEspeciePez(
                    especiePezService, "Anchoveta", "Engraulis ringens", false
            );
            EspeciePez caballa = crearEspeciePez(
                    especiePezService, "Caballa", "Scomber japonicus", true
            );
            EspeciePez bonito = crearEspeciePez(
                    especiePezService, "Bonito", "Sarda chiliensis", false
            );
            EspeciePez merluza = crearEspeciePez(
                    especiePezService, "Merluza", "Merluccius gayi", true
            );

            LocalDate today = LocalDate.now();
            LocalDate currentSeasonStart = today.withDayOfMonth(1);
            LocalDate pastSeasonEnd = currentSeasonStart.minusDays(1);
            LocalDate futureSeasonStart = currentSeasonStart.plusMonths(1);
            Temporada pastSeason = crearTemporada(
                    temporadaService, "Temporada anterior",
                    currentSeasonStart.minusMonths(12), pastSeasonEnd
            );
            Temporada currentSeason = crearTemporada(
                    temporadaService, "Temporada de Primavera",
                    currentSeasonStart, currentSeasonStart.plusMonths(1).minusDays(1)
            );
            Temporada futureSeason = crearTemporada(
                    temporadaService, "Temporada siguiente",
                    futureSeasonStart, futureSeasonStart.plusMonths(3).minusDays(1)
            );

            crearCuotaPesca(cuotaPescaService, anchoveta, pastSeason, 1200.0);
            crearCuotaPesca(cuotaPescaService, caballa, pastSeason, 450.0);
            crearCuotaPesca(cuotaPescaService, bonito, currentSeason, 700.0);
            crearCuotaPesca(cuotaPescaService, anchoveta, currentSeason, 1800.0);
            crearCuotaPesca(cuotaPescaService, caballa, currentSeason, 500.0);
            crearCuotaPesca(cuotaPescaService, merluza, currentSeason, 300.0);
            crearCuotaPesca(cuotaPescaService, anchoveta, futureSeason, 2000.0);
            crearCuotaPesca(cuotaPescaService, bonito, futureSeason, 900.0);

            LocalDateTime now = LocalDateTime.now();
            Faena callaoTrip = crearFaenaEnCurso(
                    faenaService, bitacoraService, fisherman, boatOne, callao,
                    now.plusDays(1), -12.0510, -77.2010,
                    "Salida desde Callao; se registra la zona de pesca"
            );
            registrarBitacora(
                    bitacoraService, fisherman, callaoTrip, callao,
                    -12.0520, -77.2020, "Incidencia: peligro por red rota y lobo marino"
            );
            finalizarFaena(faenaService, fisherman, callaoTrip);

            Faena huachoTrip = crearFaenaEnCurso(
                    faenaService, bitacoraService, fisherman, boatTwo, huacho,
                    now.plusDays(2), -11.1020, -77.6020,
                    "Faena exitosa con oleaje moderado"
            );
            finalizarFaena(faenaService, fisherman, huachoTrip);

            Faena piscoTrip = crearFaenaEnCurso(
                    faenaService, bitacoraService, secondFisherman, boatThree, pisco,
                    now.plusDays(3), -13.7020, -76.3020,
                    "Captura abundante en condiciones seguras"
            );
            registrarBitacora(
                    bitacoraService, secondFisherman, piscoTrip, callao,
                    -12.0530, -77.2030, "Incidencia: se reporta una red rota en la zona"
            );
            registrarBitacora(
                    bitacoraService, secondFisherman, piscoTrip, pisco,
                    -13.7040, -76.3040, "Continuación de faena en la zona de Pisco"
            );
            finalizarFaena(faenaService, secondFisherman, piscoTrip);

            Faena activeTrip = crearFaenaEnCurso(
                    faenaService, bitacoraService, fisherman, boatOne, pisco,
                    now.plusDays(4), -13.7100, -76.3100,
                    "Faena en curso para pruebas de actualización"
            );
            crearFaenaPlanificada(faenaService, fisherman, boatTwo, now.plusDays(5));

            agregarCaptura(detalleCapturaService, bitacoraRepository, callaoTrip, anchoveta, 420.0);
            agregarCaptura(detalleCapturaService, bitacoraRepository, callaoTrip, caballa, 35.0);
            agregarCaptura(detalleCapturaService, bitacoraRepository, huachoTrip, anchoveta, 230.0);
            agregarCaptura(detalleCapturaService, bitacoraRepository, huachoTrip, bonito, 180.0);
            agregarCaptura(detalleCapturaService, bitacoraRepository, huachoTrip, merluza, 22.0);
            agregarCaptura(detalleCapturaService, bitacoraRepository, piscoTrip, anchoveta, 110.0);
            agregarCaptura(detalleCapturaService, bitacoraRepository, piscoTrip, bonito, 310.0);
            agregarCaptura(detalleCapturaService, bitacoraRepository, piscoTrip, caballa, 18.0);
            agregarCaptura(detalleCapturaService, bitacoraRepository, activeTrip, anchoveta, 95.0);
            agregarCaptura(detalleCapturaService, bitacoraRepository, activeTrip, merluza, 12.0);

            agregarGastos(gastoService, callaoTrip, 34.0, 540.0, 125.0);
            agregarGastos(gastoService, huachoTrip, 46.0, 760.0, 180.0);
            agregarGastos(gastoService, piscoTrip, 29.0, 470.0, 95.0);
            agregarGastos(gastoService, activeTrip, 18.0, 290.0, 80.0);

            agregarCondicionClimatica(condicionService, bitacoraRepository, callaoTrip, 18.5, 36.0, "Oleaje moderado");
            agregarCondicionClimatica(condicionService, bitacoraRepository, huachoTrip, 21.0, 17.0, "Mar tranquilo");
            agregarCondicionClimatica(condicionService, bitacoraRepository, piscoTrip, 16.5, 42.0, "Oleaje fuerte");
            agregarCondicionClimatica(condicionService, bitacoraRepository, activeTrip, 19.0, 12.0, "Mar tranquilo");

            agregarRecommendation(
                    recomendacionIAService, callaoTrip, "ALTO",
                    "Riesgo alto: viento fuerte e incidencia reportada.",
                    now
            );
            agregarRecommendation(
                    recomendacionIAService, huachoTrip, "MEDIO",
                    "Mantener vigilancia por cambios en el oleaje.",
                    now
            );
            agregarRecommendation(
                    recomendacionIAService, piscoTrip, "BAJO",
                    "Condiciones estables para la operación.",
                    now
            );

            SecurityContextHolder.clearContext();
        };
    }

    private static Authority obtenerAuthority(
            AuthorityRepository authorityRepository,
            String name) {
        return requireCreated(
                authorityRepository.findFirstByNameOrderByIdAsc(name),
                "rol " + name + " (debe inicializarse en SecurityDataInitializer)"
        );
    }

    private static User registrarUser(
            UserService userService,
            String nombres,
            String apellidos,
            String correo,
            String password) {
        UserRegistroDTO request = new UserRegistroDTO();
        request.setNombres(nombres);
        request.setApellidos(apellidos);
        request.setCorreo(correo);
        request.setPassword(password);
        UserDTO saved = requireCreated(userService.registrar(request), "usuario " + correo);
        return requireCreated(userService.buscarPorId(saved.getId()), "usuario " + correo);
    }

    private static void asignarRoles(
            UserRepository userRepository,
            User user,
            Authority... roles) {
        user.setAuthorities(List.of(roles));
        userRepository.save(user);
    }

    private static Embarcacion crearEmbarcacion(
            EmbarcacionService service,
            String name,
            String registration,
            double capacity,
            User owner) {
        EmbarcacionRequestDTO request = new EmbarcacionRequestDTO();
        request.setNombre(name);
        request.setMatricula(registration);
        request.setCapacidadToneladas(capacity);
        request.setUsuarioId(owner.getId());
        return requireCreated(service.insertar(request), "embarcación " + registration);
    }

    private static ZonaPesca crearZonaPesca(
            ZonaPescaService service,
            String name,
            double latitude,
            double longitude,
            double distanceToCoast,
            double radius) {
        ZonaPescaRequestDTO request = new ZonaPescaRequestDTO();
        request.setNombreZona(name);
        request.setLatitud(latitude);
        request.setLongitud(longitude);
        request.setDistanciaCostaKm(distanceToCoast);
        request.setRadioAreaKm(radius);
        return requireCreated(service.insertar(request), "zona " + name);
    }

    private static EspeciePez crearEspeciePez(
            EspeciePezService service,
            String commonName,
            String scientificName,
            boolean closedSeason) {
        EspeciePezRequestDTO request = new EspeciePezRequestDTO();
        request.setNombreComun(commonName);
        request.setNombreCientifico(scientificName);
        request.setEstadoVeda(closedSeason);
        return requireCreated(service.insertar(request), "especie " + commonName);
    }

    private static Temporada crearTemporada(
            TemporadaService service,
            String name,
            LocalDate start,
            LocalDate end) {
        TemporadaRequestDTO request = new TemporadaRequestDTO();
        request.setNombreTemporada(name);
        request.setFechaInicio(start);
        request.setFechaFin(end);
        return requireCreated(service.insertar(request), "temporada " + name);
    }

    private static void crearCuotaPesca(
            CuotaPescaService service,
            EspeciePez species,
            Temporada season,
            double limit) {
        CuotaPescaRequestDTO quota = new CuotaPescaRequestDTO();
        quota.setEspecieId(species.getId());
        quota.setTemporadaId(season.getId());
        quota.setLimiteToneladasIndustrial(limit);
        requireCreated(service.insertarCuota(quota), "cuota de " + species.getNombreComun());
    }

    private static Faena crearFaenaEnCurso(
            FaenaService faenaService,
            BitacoraService bitacoraService,
            User owner,
            Embarcacion boat,
            ZonaPesca zone,
            LocalDateTime departure,
            double latitude,
            double longitude,
            String observations) {
        Authentication previousAuthentication = SecurityContextHolder.getContext().getAuthentication();
        autenticarComo(owner);
        try {
            Faena faena = crearFaenaPlanificada(faenaService, owner, boat, departure);
            faenaService.cambiarEstadoAEnCurso(faena.getId());
            registrarBitacora(bitacoraService, owner, faena, zone, latitude, longitude, observations);
            return faena;
        } finally {
            restaurarAutenticacion(previousAuthentication);
        }
    }

    private static Faena crearFaenaPlanificada(
            FaenaService service,
            User owner,
            Embarcacion boat,
            LocalDateTime departure) {
        Authentication previousAuthentication = SecurityContextHolder.getContext().getAuthentication();
        autenticarComo(owner);
        try {
            FaenaRequestDTO request = new FaenaRequestDTO();
            request.setEmbarcacionId(boat.getId());
            request.setFechaHoraSalidaProgramada(departure);
            request.setFechaHoraLlegadaEstimada(departure.plusHours(4));
            return requireCreated(service.insertarFaena(request), "faena de " + owner.getEmail());
        } finally {
            restaurarAutenticacion(previousAuthentication);
        }
    }

    private static void registrarBitacora(
            BitacoraService service,
            User owner,
            Faena faena,
            ZonaPesca zone,
            double latitude,
            double longitude,
            String observations) {
        Authentication previousAuthentication = SecurityContextHolder.getContext().getAuthentication();
        autenticarComo(owner);
        try {
            BitacoraRequestDTO request = new BitacoraRequestDTO();
            request.setFaenaId(faena.getId());
            request.setZonaPescaId(zone.getId());
            request.setLatitud(latitude);
            request.setLongitud(longitude);
            request.setObservaciones(observations);
            requireCreated(service.insertarBitacora(request), "bitácora de la faena " + faena.getId());
        } finally {
            restaurarAutenticacion(previousAuthentication);
        }
    }

    private static void finalizarFaena(FaenaService service, User owner, Faena faena) {
        Authentication previousAuthentication = SecurityContextHolder.getContext().getAuthentication();
        autenticarComo(owner);
        try {
            service.terminarFaena(faena.getId(), false, null);
        } finally {
            restaurarAutenticacion(previousAuthentication);
        }
    }

    private static void autenticarComo(User user) {
        UserSecurity userSecurity = new UserSecurity(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userSecurity, null, userSecurity.getAuthorities()));
    }

    private static void restaurarAutenticacion(Authentication authentication) {
        if (authentication == null) {
            SecurityContextHolder.clearContext();
        } else {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private static void agregarCaptura(
            DetalleCapturaService service,
            BitacoraRepository bitacoraRepository,
            Faena faena,
            EspeciePez species,
            double volumeKg) {
        Bitacora bitacora = bitacoraRepository
                .findFirstByFaenaIdOrderByFechaHoraRegistroDesc(faena.getId())
                .orElseThrow();
        DetalleCapturaRequestDTO capture = new DetalleCapturaRequestDTO();
        capture.setEspecieId(species.getId());
        capture.setVolumenKg(volumeKg);
        service.insertarCaptura(bitacora.getId(), capture);
    }

    private static void agregarGastos(
            GastoOperativoService service,
            Faena faena,
            double fuelGallons,
            double fuelCost,
            double suppliesCost) {
        GastoOperativoRequestDTO expenses = new GastoOperativoRequestDTO();
        expenses.setGalonesCombustible(fuelGallons);
        expenses.setCostoCombustible(fuelCost);
        expenses.setCostoHieloInsumos(suppliesCost);
        service.insertarGasto(faena.getId(), expenses);
    }

    private static void agregarCondicionClimatica(
            CondicionClimaticaService service,
            BitacoraRepository bitacoraRepository,
            Faena faena,
            double temperature,
            double windSpeed,
            String waveState) {
        CondicionClimaticaRequestDTO request = new CondicionClimaticaRequestDTO();
        request.setTemperaturaCelsius(temperature);
        request.setVelocidadVientoNudos(windSpeed);
        request.setEstadoOleaje(waveState);
        Bitacora bitacora = bitacoraRepository.findFirstByFaenaIdOrderByFechaHoraRegistroDesc(faena.getId())
                .orElseThrow();
        service.insertarCondicionesPorBitacora(bitacora.getId(), request);

    }

    private static void agregarRecommendation(
            RecomendacionIAService service,
            Faena faena,
            String risk,
            String analysis,
            LocalDateTime generatedAt) {
        RecomendacionIA recommendation = new RecomendacionIA();
        recommendation.setFaena(faena);
        recommendation.setNivelRiesgo(risk);
        recommendation.setAnalisisTexto(analysis);
        recommendation.setFechaGeneracion(generatedAt);
        service.insertarRecomendaciones(recommendation);
    }

    private static <T> T requireCreated(T value, String description) {
        return Objects.requireNonNull(
                value,
                "No se pudo crear el registro de prueba: " + description
        );
    }
}