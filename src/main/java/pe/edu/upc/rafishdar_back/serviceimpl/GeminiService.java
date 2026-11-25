package pe.edu.upc.rafishdar_back.serviceimpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.fallback-url}")
    private String fallbackUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** Envía un prompt simple a Gemini con fallback automático */
    public String generarTexto(String prompt) {
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of("maxOutputTokens", 500, "temperature", 0.4)
        );
        return ejecutarConFallback(body);
    }

    /** Versión multi-turno con historial (role: user/model, text) */
    public String generarTextoConHistorial(List<Map<String, String>> messages) {
        List<Map<String, Object>> contents = messages.stream()
                .map(m -> Map.<String, Object>of(
                        "role", m.get("role"),
                        "parts", List.of(Map.of("text", m.get("text")))
                ))
                .toList();

        Map<String, Object> body = Map.of(
                "contents", contents,
                "generationConfig", Map.of("maxOutputTokens", 600, "temperature", 0.5)
        );
        return ejecutarConFallback(body);
    }

    /** Versión con prompt de sistema enriquecido con datos del usuario */
    public String generarTextoConContexto(List<Map<String, String>> messages, String contextoUsuario) {
        List<Map<String, Object>> contents = new ArrayList<>();
        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", contextoUsuario + "\n\nConfirma que entendiste tu rol y los datos disponibles."))
        ));
        contents.add(Map.of(
                "role", "model",
                "parts", List.of(Map.of("text", "Entendido. Tengo acceso a tus datos históricos de faenas y capturas. ¿En qué te ayudo?"))
        ));
        messages.forEach(m -> contents.add(Map.of(
                "role", m.get("role"),
                "parts", List.of(Map.of("text", m.get("text")))
        )));

        Map<String, Object> body = Map.of(
                "contents", contents,
                "generationConfig", Map.of("maxOutputTokens", 700, "temperature", 0.5)
        );
        return ejecutarConFallback(body);
    }

    private String ejecutarConFallback(Map<String, Object> body) {
        List<String> urls = List.of(
                apiUrl,
                fallbackUrl,
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent",
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent"
        );

        String ultimoError = "";
        for (String url : urls) {
            if (url == null || url.isBlank()) continue;
            try {
                return llamarGemini(url.trim(), body);
            } catch (HttpStatusCodeException httpEx) {
                ultimoError = "HTTP " + httpEx.getStatusCode().value() + ": " + httpEx.getResponseBodyAsString();
                log.error("Error HTTP llamando a Gemini ({}): {}", url, ultimoError);
            } catch (Exception ex) {
                ultimoError = ex.getMessage();
                log.error("Error de conexión llamando a Gemini ({}): {}", url, ultimoError);
            }
        }
        return "No se pudo obtener respuesta de Gemini (" + resumirError(ultimoError) + "). Verifica la API Key en application.properties.";
    }

    private String llamarGemini(String baseUrl, Map<String, Object> body) throws Exception {
        String cleanKey = apiKey != null ? apiKey.trim() : "";
        if (cleanKey.isEmpty()) {
            throw new IllegalStateException("La propiedad gemini.api.key está vacía");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Header oficial requerido por Google AI Studio para claves modernas (AQ. y AIza)
        headers.set("x-goog-api-key", cleanKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, entity, String.class);

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode candidates = root.path("candidates");
        if (candidates.isArray() && !candidates.isEmpty()) {
            JsonNode parts = candidates.get(0).path("content").path("parts");
            if (parts.isArray() && !parts.isEmpty()) {
                return parts.get(0).path("text").asText("Sin respuesta de Gemini");
            }
        }
        throw new IllegalStateException("Respuesta sin contenido de Gemini");
    }

    private String resumirError(String rawError) {
        if (rawError == null || rawError.isBlank()) return "Error desconocido";
        try {
            int jsonStart = rawError.indexOf('{');
            if (jsonStart >= 0) {
                JsonNode errNode = objectMapper.readTree(rawError.substring(jsonStart));
                String msg = errNode.path("error").path("message").asText();
                if (!msg.isBlank()) return msg;
            }
        } catch (Exception ignored) {}
        return rawError.length() > 140 ? rawError.substring(0, 140) + "..." : rawError;
    }
}