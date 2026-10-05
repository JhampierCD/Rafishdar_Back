package pe.edu.upc.rafishdar_back.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pe.edu.upc.rafishdar_back.dtos.DetalleCapturaRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.GastoOperativoRequestDTO;
import pe.edu.upc.rafishdar_back.entities.*;
import pe.edu.upc.rafishdar_back.repositories.*;
import pe.edu.upc.rafishdar_back.services.BitacoraFaenaService;
import pe.edu.upc.rafishdar_back.services.DetalleCapturaService;
import pe.edu.upc.rafishdar_back.services.GastoOperativoService;

import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmbarcacionRepository embarcacionRepository;
    private final ZonaPescaRepository zonaRepository;
    private final EspeciePezRepository especieRepository;
    private final BitacoraFaenaService bitacoraService;
    private final DetalleCapturaService detalleCapturaService;
    private final GastoOperativoService gastoService;

    public DataSeeder(UserRepository userRepository, EmbarcacionRepository embarcacionRepository,
                      ZonaPescaRepository zonaRepository, EspeciePezRepository especieRepository,
                      BitacoraFaenaService bitacoraService, DetalleCapturaService detalleCapturaService,
                      GastoOperativoService gastoService) {
        this.userRepository = userRepository;
        this.embarcacionRepository = embarcacionRepository;
        this.zonaRepository = zonaRepository;
        this.especieRepository = especieRepository;
        this.bitacoraService = bitacoraService;
        this.detalleCapturaService = detalleCapturaService;
        this.gastoService = gastoService;
    }

    @Override
    public void run(String... args) throws Exception {

        // 1. Crear User
        User user = null;
        if (userRepository.count() == 0) {
            user = new User();
            user.setNombres("Carlos");
            user.setApellidos("Mendoza");
            user.setCorreo("carlos@pesca.com");
            user.setPassword("123456");
            user.setEstado("ACTIVO");
            user = userRepository.save(user); // Guardamos y recuperamos con ID
            System.out.println("✅ User de prueba insertado.");
        } else {
            user = userRepository.findById(1L).orElse(null);
        }

        // 2. Crear Embarcacion (Vinculada al User)
        Embarcacion emb = null;
        if (embarcacionRepository.count() == 0 && user != null) {
            emb = new Embarcacion();
            emb.setNombre("La Niña II");
            emb.setMatricula("CE-12345-PM");
            emb.setCapacidadToneladas(15.5);
            emb.setUsuario(user); // Relación FK
            embarcacionRepository.save(emb);
            System.out.println("✅ Embarcación de prueba insertada.");
        }

        // 3. Crear Zonas de Pesca
        ZonaPesca z1 = null;
        ZonaPesca z2 = null;
        if (zonaRepository.count() == 0) {
            z1 = new ZonaPesca();
            z1.setNombreZona("Zona Norte - Paita");
            z1.setLatitud(-5.0833);
            z1.setLongitud(-81.1167);
            z1.setDistanciaCostaKm(15.5);
            z1.setRadioAreaKm(25.0);
            zonaRepository.save(z1);

            z2 = new ZonaPesca();
            z2.setNombreZona("Zona Centro - Callao");
            z2.setLatitud(-12.0500);
            z2.setLongitud(-77.2000);
            z2.setDistanciaCostaKm(8.0);
            z2.setRadioAreaKm(30.0);
            zonaRepository.save(z2);
            System.out.println("✅ Zonas de pesca insertadas.");
        }

        // 4. Crear Especies
        EspeciePez e1 = null;
        EspeciePez e2 = null;
        if (especieRepository.count() == 0) {
            e1 = new EspeciePez();
            e1.setNombreComun("Anchoveta");
            e1.setEstadoVeda(false);
            especieRepository.save(e1);

            e2 = new EspeciePez();
            e2.setNombreComun("Jurel");
            e2.setEstadoVeda(true); // Ejemplo en veda
            especieRepository.save(e2);
            System.out.println("✅ Especies insertadas.");
        }

        // 5. Crear Bitácora de Faena inicial (En curso para poder probar coordenadas y gastos)
        BitacoraFaena bitacora = new BitacoraFaena();
        bitacora.setUsuario(user);
        bitacora.setEmbarcacion(emb);
        bitacora.setZonaPesca(z1);
        bitacora.setEstado("En curso");
        bitacora.setFechaHoraSalida(LocalDateTime.of(2026, 10, 5, 4, 30));
        bitacora.setLatitudPesca(-12.0800);
        bitacora.setLongitudPesca(-77.2200);
        bitacora.setObservaciones("Faena de prueba con navegación en marcha");
        BitacoraFaena bitacoraGuardada = bitacoraService.insertarBitacora(bitacora);

        // 6. Crear Detalle de Captura usando el DTO
        DetalleCapturaRequestDTO capturaDTO = new DetalleCapturaRequestDTO();
        capturaDTO.setIdEspecie(e1.getId());
        capturaDTO.setVolumenKg(450.0); // Ajustado al campo que lee tu metodo
        // Si tu DTO tiene los otros campos (tallas, incidental), agrégalos aquí también.

        detalleCapturaService.insertarCaptura(bitacoraGuardada.getId(), capturaDTO);

        // 7. Crear Gasto Operativo usando el DTO
        GastoOperativoRequestDTO gastoDTO = new GastoOperativoRequestDTO();
        gastoDTO.setGalonesCombustible(35.0);
        gastoDTO.setCostoCombustible(560.0);
        gastoDTO.setCostoHieloInsumos(110.0);

        gastoService.insertarGasto(bitacoraGuardada.getId(), gastoDTO);
    }
}