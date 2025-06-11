package software.sebastian.oposiciones.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ConvocatoriaExtractor {

    // Number mapping for Spanish words to numbers
    private static final Map<String, Integer> NUMBER_MAP = new HashMap<>();
    static {
        NUMBER_MAP.put("cero", 0);
        NUMBER_MAP.put("uno", 1);
        NUMBER_MAP.put("una", 1);
        NUMBER_MAP.put("dos", 2);
        NUMBER_MAP.put("tres", 3);
        NUMBER_MAP.put("cuatro", 4);
        NUMBER_MAP.put("cinco", 5);
        NUMBER_MAP.put("seis", 6);
        NUMBER_MAP.put("siete", 7);
        NUMBER_MAP.put("ocho", 8);
        NUMBER_MAP.put("nueve", 9);
        NUMBER_MAP.put("diez", 10);
        NUMBER_MAP.put("once", 11);
        NUMBER_MAP.put("doce", 12);
        NUMBER_MAP.put("trece", 13);
        NUMBER_MAP.put("catorce", 14);
        NUMBER_MAP.put("quince", 15);
        NUMBER_MAP.put("veinte", 20);
        NUMBER_MAP.put("veinticinco", 25);
        NUMBER_MAP.put("treinta", 30);
        NUMBER_MAP.put("cuarenta", 40);
        NUMBER_MAP.put("cincuenta", 50);
        NUMBER_MAP.put("sesenta", 60);
        NUMBER_MAP.put("setenta", 70);
        NUMBER_MAP.put("ochenta", 80);
        NUMBER_MAP.put("noventa", 90);
        NUMBER_MAP.put("cien", 100);
    }

    // Patterns for extracting information
    private static final Pattern PLAZAS_PATTERN = Pattern.compile(
        "(?:se convocan?|convoca(?:toria)?\\s+de)\\s+(\\d+|\\w+)\\s+plazas?",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern DENOMINACION_PATTERN = Pattern.compile(
        "(?:denominación|cuerpo|escala|categoría)\\s*:?\\s*([^\\n.]+)",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern LOCALIDAD_PATTERN = Pattern.compile(
        "(?:localidad|provincia|destino)\\s*:?\\s*([^\\n.]+)",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PLAZO_PATTERN = Pattern.compile(
        "(?:plazo\\s+de\\s+(?:presentación|solicitud)|presentar\\s+solicitudes?)\\s*:?\\s*([^\\n.]+)",
        Pattern.CASE_INSENSITIVE
    );

    public Map<String, Object> extractFields(String text) {
        Map<String, Object> result = new HashMap<>();
        List<String> warnings = new ArrayList<>();

        String tipo = classifyConvocation(text);
        result.put("tipo", tipo);

        // Extract órgano
        String organo = extractOrgano(text, tipo);
        if (organo != null && !organo.isEmpty()) {
            result.put("órgano", organo);
        }

        // Extract número de plazas
        String plazas = extractPlazas(text);
        if (plazas != null && !plazas.isEmpty()) {
            result.put("plazas", plazas);
        } else {
            warnings.add("No se pudo extraer el número de plazas");
        }

        // Extract denominación
        String denominacion = extractDenominacion(text);
        if (denominacion != null && !denominacion.isEmpty()) {
            result.put("denominación", denominacion);
        }

        // Extract localidad/provincia
        String localidad = extractLocalidad(text);
        if (localidad != null && !localidad.isEmpty()) {
            result.put("localidad", localidad);
        }

        // Extract plazo
        String plazo = extractPlazo(text);
        if (plazo != null && !plazo.isEmpty()) {
            result.put("plazo", plazo);
        }

        if (!warnings.isEmpty()) {
            result.put("warnings", warnings);
        }

        return result;
    }

    private String classifyConvocation(String text) {
        String lowerText = text.toLowerCase();
        
        if (containsAny(lowerText, "ayuntamiento de", "diputación", "cabildo", 
                       "consell insular", "mancomunidad", "administración local")) {
            return "municipal";
        } else if (containsAny(lowerText, "universidad de", "consorcio", "empresa pública")) {
            return "otros";
        } else if (containsAny(lowerText, "ministerio de", "subsecretaría", 
                              "secretaría de estado", "dirección general", 
                              "guardia civil", "policía nacional", "cuerpo de")) {
            return "estatal";
        } else {
            return "otros";
        }
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String extractOrgano(String text, String tipo) {
        Pattern pattern = null;
        
        switch (tipo) {
            case "municipal":
                pattern = Pattern.compile(
                    "(Ayuntamiento\\s+de\\s+[^\\n,.]+|Diputación\\s+[^\\n,.]+|Cabildo\\s+[^\\n,.]+)",
                    Pattern.CASE_INSENSITIVE
                );
                break;
            case "estatal":
                pattern = Pattern.compile(
                    "(Ministerio\\s+de\\s+[^\\n,.]+|Secretaría\\s+[^\\n,.]+|Dirección\\s+General\\s+[^\\n,.]+)",
                    Pattern.CASE_INSENSITIVE
                );
                break;
            default:
                pattern = Pattern.compile(
                    "(Universidad\\s+de\\s+[^\\n,.]+|Consorcio\\s+[^\\n,.]+|[^\\n,.]+(?=\\s+convoca))",
                    Pattern.CASE_INSENSITIVE
                );
        }
        
        if (pattern != null) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        
        return null;
    }

    private String extractPlazas(String text) {
        Matcher matcher = PLAZAS_PATTERN.matcher(text);
        if (matcher.find()) {
            String plazasStr = matcher.group(1).trim();
            
            // Try to convert word to number
            Integer number = NUMBER_MAP.get(plazasStr.toLowerCase());
            if (number != null) {
                return number.toString();
            }
            
            return plazasStr;
        }
        return null;
    }

    private String extractDenominacion(String text) {
        Matcher matcher = DENOMINACION_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String extractLocalidad(String text) {
        Matcher matcher = LOCALIDAD_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String extractPlazo(String text) {
        Matcher matcher = PLAZO_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
}