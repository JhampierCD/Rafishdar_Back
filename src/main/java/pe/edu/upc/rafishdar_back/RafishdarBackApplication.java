package pe.edu.upc.rafishdar_back;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import pe.edu.upc.rafishdar_back.dtos.CuotaPescaDTO;
import pe.edu.upc.rafishdar_back.dtos.DetalleCapturaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.EspeciePezDTO;
import pe.edu.upc.rafishdar_back.dtos.GastoOperativoRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.TemporadaDTO;
import pe.edu.upc.rafishdar_back.dtos.UserDTO;
import pe.edu.upc.rafishdar_back.dtos.UserRegistroDTO;
import pe.edu.upc.rafishdar_back.entities.Authority;
import pe.edu.upc.rafishdar_back.entities.BitacoraFaena;
import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;
import pe.edu.upc.rafishdar_back.entities.Embarcacion;
import pe.edu.upc.rafishdar_back.entities.RecomendacionIA;
import pe.edu.upc.rafishdar_back.entities.User;
import pe.edu.upc.rafishdar_back.entities.ZonaPesca;
import pe.edu.upc.rafishdar_back.repositories.BitacoraFaenaRepository;
import pe.edu.upc.rafishdar_back.repositories.UserRepository;
import pe.edu.upc.rafishdar_back.services.AuthorityService;
import pe.edu.upc.rafishdar_back.services.BitacoraFaenaService;
import pe.edu.upc.rafishdar_back.services.CondicionClimaticaService;
import pe.edu.upc.rafishdar_back.services.CuotaPescaService;
import pe.edu.upc.rafishdar_back.services.DetalleCapturaService;
import pe.edu.upc.rafishdar_back.services.EmbarcacionService;
import pe.edu.upc.rafishdar_back.services.EspeciePezService;
import pe.edu.upc.rafishdar_back.services.GastoOperativoService;
import pe.edu.upc.rafishdar_back.services.RecomendacionIAService;
import pe.edu.upc.rafishdar_back.services.TemporadaService;
import pe.edu.upc.rafishdar_back.services.UserService;
import pe.edu.upc.rafishdar_back.services.ZonaPescaService;

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
    public CommandLineRunner startConfiguration(
            AuthorityService authorityService,
            BitacoraFaenaService bitacoraService,
            BitacoraFaenaRepository bitacoraRepository,
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
            Authority adminRole = crearAuthority(authorityService, "ADMIN");
            Authority fishermanRole = crearAuthority(authorityService, "PESCADOR");
            Authority technicianRole = crearAuthority(authorityService, "TECNICO");

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

            EspeciePezDTO anchoveta = crearEspeciePez(
                    especiePezService, "Anchoveta", "Engraulis ringens", false
            );
            EspeciePezDTO caballa = crearEspeciePez(
                    especiePezService, "Caballa", "Scomber japonicus", true
            );
            EspeciePezDTO bonito = crearEspeciePez(
                    especiePezService, "Bonito", "Sarda chiliensis", false
            );
            EspeciePezDTO merluza = crearEspeciePez(
                    especiePezService, "Merluza", "Merluccius gayi", true
            );

            LocalDate today = LocalDate.now();
            LocalDate currentSeasonStart = today.withDayOfMonth(1);
            LocalDate pastSeasonEnd = currentSeasonStart.minusDays(1);
            LocalDate futureSeasonStart = currentSeasonStart.plusMonths(1);
            TemporadaDTO pastSeason = crearTemporada(
                    temporadaService, "Temporada anterior",
                    currentSeasonStart.minusMonths(12), pastSeasonEnd
            );
            TemporadaDTO currentSeason = crearTemporada(
                    temporadaService, "Temporada de Primavera",
                    currentSeasonStart, currentSeasonStart.plusMonths(1).minusDays(1)
            );
            TemporadaDTO futureSeason = crearTemporada(
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
            BitacoraFaena callaoTrip = crearBitacoraFaena(
                    bitacoraService, fisherman, boatOne, callao,
                    now.minusDays(1)
            );
            BitacoraFaena huachoTrip = crearBitacoraFaena(
                    bitacoraService, fisherman, boatTwo, huacho,
                    now.minusDays(2)
            );
            BitacoraFaena piscoTrip = crearBitacoraFaena(
                    bitacoraService, secondFisherman, boatThree, pisco,
                    now.minusDays(3)
            );
            BitacoraFaena activeTrip = crearBitacoraFaena(
                    bitacoraService, fisherman, boatOne, pisco,
                    now.plusHours(1)
            );
            crearBitacoraFaena(
                    bitacoraService, secondFisherman, boatThree, callao,
                    now.plusDays(1)
            );

            terminarBitacoraFaena(
                    bitacoraService, bitacoraRepository, callaoTrip,
                    now.minusHours(4), now.minusHours(2),
                    -12.0510, -77.2010,
                    "Incidencia: peligro por red rota y lobo marino"
            );
            terminarBitacoraFaena(
                    bitacoraService, bitacoraRepository, huachoTrip,
                    now.minusHours(7), now.minusHours(4),
                    -11.1020, -77.6020,
                    "Faena exitosa con oleaje moderado"
            );
            terminarBitacoraFaena(
                    bitacoraService, bitacoraRepository, piscoTrip,
                    now.minusHours(6), now.minusHours(3),
                    -13.7020, -76.3020,
                    "Captura abundante en condiciones seguras"
            );
            bitacoraService.cambiarEstadoAEnCurso(activeTrip.getId());
            BitacoraFaena activeChanges = new BitacoraFaena();
            activeChanges.setLatitudPesca(-13.7100);
            activeChanges.setLongitudPesca(-76.3100);
            activeChanges.setObservaciones("Faena en curso para pruebas de actualización");
            bitacoraService.actualizarEnCurso(activeTrip.getId(), activeChanges);

            agregarCaptura(detalleCapturaService, callaoTrip, anchoveta, 420.0);
            agregarCaptura(detalleCapturaService, callaoTrip, caballa, 35.0);
            agregarCaptura(detalleCapturaService, huachoTrip, anchoveta, 230.0);
            agregarCaptura(detalleCapturaService, huachoTrip, bonito, 180.0);
            agregarCaptura(detalleCapturaService, huachoTrip, merluza, 22.0);
            agregarCaptura(detalleCapturaService, piscoTrip, anchoveta, 110.0);
            agregarCaptura(detalleCapturaService, piscoTrip, bonito, 310.0);
            agregarCaptura(detalleCapturaService, piscoTrip, caballa, 18.0);
            agregarCaptura(detalleCapturaService, activeTrip, anchoveta, 95.0);
            agregarCaptura(detalleCapturaService, activeTrip, merluza, 12.0);

            agregarGastos(gastoService, callaoTrip, 34.0, 540.0, 125.0);
            agregarGastos(gastoService, huachoTrip, 46.0, 760.0, 180.0);
            agregarGastos(gastoService, piscoTrip, 29.0, 470.0, 95.0);
            agregarGastos(gastoService, activeTrip, 18.0, 290.0, 80.0);

            agregarCondicionClimatica(condicionService, callaoTrip, 18.5, 36.0, "Oleaje moderado");
            agregarCondicionClimatica(condicionService, huachoTrip, 21.0, 17.0, "Mar tranquilo");
            agregarCondicionClimatica(condicionService, piscoTrip, 16.5, 42.0, "Oleaje fuerte");
            agregarCondicionClimatica(condicionService, activeTrip, 19.0, 12.0, "Mar tranquilo");

            agregarRecommendation(
                    recomendacionIAService, callaoTrip, "ALTO",
                    "Riesgo alto: viento fuerte e incidencia reportada.",
                    now.minusHours(1)
            );
            agregarRecommendation(
                    recomendacionIAService, huachoTrip, "MEDIO",
                    "Mantener vigilancia por cambios en el oleaje.",
                    now.minusHours(3)
            );
            agregarRecommendation(
                    recomendacionIAService, piscoTrip, "BAJO",
                    "Condiciones estables para la operación.",
                    now.minusHours(2)
            );

        };
    }

    private static Authority crearAuthority(
            AuthorityService authorityService,
            String name) {
        Authority authority = new Authority();
        authority.setName(name);
        return requireCreated(authorityService.insertar(authority), "rol " + name);
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
        Embarcacion boat = new Embarcacion();
        boat.setNombre(name);
        boat.setMatricula(registration);
        boat.setCapacidadToneladas(capacity);
        boat.setUsuario(owner);
        return requireCreated(service.insertar(boat), "embarcación " + registration);
    }

    private static ZonaPesca crearZonaPesca(
            ZonaPescaService service,
            String name,
            double latitude,
            double longitude,
            double distanceToCoast,
            double radius) {
        ZonaPesca zone = new ZonaPesca();
        zone.setNombreZona(name);
        zone.setLatitud(latitude);
        zone.setLongitud(longitude);
        zone.setDistanciaCostaKm(distanceToCoast);
        zone.setRadioAreaKm(radius);
        return requireCreated(service.insertar(zone), "zona " + name);
    }

    private static EspeciePezDTO crearEspeciePez(
            EspeciePezService service,
            String commonName,
            String scientificName,
            boolean closedSeason) {
        EspeciePezDTO species = new EspeciePezDTO();
        species.setNombreComun(commonName);
        species.setNombreCientifico(scientificName);
        species.setEstadoVeda(closedSeason);
        return requireCreated(service.insertar(species), "especie " + commonName);
    }

    private static TemporadaDTO crearTemporada(
            TemporadaService service,
            String name,
            LocalDate start,
            LocalDate end) {
        TemporadaDTO season = new TemporadaDTO();
        season.setNombreTemporada(name);
        season.setFechaInicio(start);
        season.setFechaFin(end);
        return requireCreated(service.insertar(season), "temporada " + name);
    }

    private static void crearCuotaPesca(
            CuotaPescaService service,
            EspeciePezDTO species,
            TemporadaDTO season,
            double limit) {
        CuotaPescaDTO quota = new CuotaPescaDTO();
        quota.setEspecieId(species.getId());
        quota.setTemporadaId(season.getId());
        quota.setLimiteToneladasIndustrial(limit);
        requireCreated(service.insertar(quota), "cuota de " + species.getNombreComun());
    }

    private static BitacoraFaena crearBitacoraFaena(
            BitacoraFaenaService service,
            User owner,
            Embarcacion boat,
            ZonaPesca zone,
            LocalDateTime departure) {
        BitacoraFaena trip = new BitacoraFaena();
        trip.setUsuario(owner);
        trip.setEmbarcacion(boat);
        trip.setZonaPesca(zone);
        trip.setFechaHoraSalida(departure);
        return requireCreated(service.insertarBitacora(trip), "bitácora");
    }

    private static void terminarBitacoraFaena(
            BitacoraFaenaService service,
            BitacoraFaenaRepository repository,
            BitacoraFaena trip,
            LocalDateTime departure,
            LocalDateTime arrival,
            double latitude,
            double longitude,
            String observations) {
        service.cambiarEstadoAEnCurso(trip.getId());
        service.terminarBitacora(trip.getId(), observations);
        BitacoraFaena finished = service.buscarBitacoraPorId(trip.getId());
        finished.setFechaHoraSalida(departure);
        finished.setFechaHoraLlegada(arrival);
        finished.setLatitudPesca(latitude);
        finished.setLongitudPesca(longitude);
        repository.save(finished);
    }

    private static void agregarCaptura(
            DetalleCapturaService service,
            BitacoraFaena trip,
            EspeciePezDTO species,
            double volumeKg) {
        DetalleCapturaRequestDTO capture = new DetalleCapturaRequestDTO();
        capture.setIdEspecie(species.getId());
        capture.setVolumenKg(volumeKg);
        service.insertarCaptura(trip.getId(), capture);
    }

    private static void agregarGastos(
            GastoOperativoService service,
            BitacoraFaena trip,
            double fuelGallons,
            double fuelCost,
            double suppliesCost) {
        GastoOperativoRequestDTO expenses = new GastoOperativoRequestDTO();
        expenses.setGalonesCombustible(fuelGallons);
        expenses.setCostoCombustible(fuelCost);
        expenses.setCostoHieloInsumos(suppliesCost);
        service.insertarGasto(trip.getId(), expenses);
    }

    private static void agregarCondicionClimatica(
            CondicionClimaticaService service,
            BitacoraFaena trip,
            double temperature,
            double windSpeed,
            String waveState) {
        CondicionClimatica weather = new CondicionClimatica();
        weather.setBitacora(trip);
        weather.setTemperaturaCelsius(temperature);
        weather.setVelocidadVientoNudos(windSpeed);
        weather.setEstadoOleaje(waveState);
        service.insertarCondiciones(weather);
    }

    private static void agregarRecommendation(
            RecomendacionIAService service,
            BitacoraFaena trip,
            String risk,
            String analysis,
            LocalDateTime generatedAt) {
        RecomendacionIA recommendation = new RecomendacionIA();
        recommendation.setBitacora(trip);
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
