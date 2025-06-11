package software.sebastian.oposiciones.service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import software.sebastian.oposiciones.model.Convocatoria;
import software.sebastian.oposiciones.repository.ConvocatoriaRepository;

@Service
public class ConvocatoriaGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(ConvocatoriaGeneratorService.class);
    private static final String RSS_URL = "https://www.boe.es/rss/canal_per.php?l=p&c=140";
    private static final Pattern BOE_ID_PATTERN = Pattern.compile("id=(BOE-A-\\d{4}-\\d+)");
    
    private final ConvocatoriaRepository convocatoriaRepository;
    private final ConvocatoriaService convocatoriaService;
    private final ConvocatoriaExtractor convocatoriaExtractor;
    private final ObjectMapper objectMapper;

    public ConvocatoriaGeneratorService(ConvocatoriaRepository convocatoriaRepository,
                                      ConvocatoriaService convocatoriaService,
                                      ConvocatoriaExtractor convocatoriaExtractor,
                                      ObjectMapper objectMapper) {
        this.convocatoriaRepository = convocatoriaRepository;
        this.convocatoriaService = convocatoriaService;
        this.convocatoriaExtractor = convocatoriaExtractor;
        this.objectMapper = objectMapper;
    }

    /**
     * Scheduled method that runs 4 times a day (every 6 hours)
     * at 00:00, 06:00, 12:00, and 18:00
     */
    @Scheduled(cron = "0 0 0,6,12,18 * * *")
    @Transactional
    public void generateConvocatorias() {
        logger.info("Starting convocatoria generation process...");
        
        try {
            processBoeRssFeed();
            logger.info("Convocatoria generation process completed successfully.");
        } catch (Exception e) {
            logger.error("Error during convocatoria generation process", e);
        }
    }

    private void processBoeRssFeed() throws Exception {
        URL feedUrl = URI.create(RSS_URL).toURL();
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl.openStream()));

        int count = 0;
        for (SyndEntry entry : feed.getEntries()) {
            if (count >= 80) {
                break;
            }
            try {
                processEntry(entry);
                ++count;
            } catch (Exception e) {
                logger.error("Error processing entry: " + entry.getLink(), e);
            }
        }
    }

    private void processEntry(SyndEntry entry) {
        // Extract BOE ID from entry link
        String boeId = extractBoeId(entry.getLink());
        if (boeId == null) {
            logger.warn("Could not extract BOE ID from link: " + entry.getLink());
            return;
        }

        // Get PDF URL from GUID
        String pdfUrl = entry.getUri();
        
        // Get publication date
        Date publishedDate = entry.getPublishedDate();
        LocalDateTime fechaPublicacion = publishedDate != null 
            ? LocalDateTime.ofInstant(publishedDate.toInstant(), ZoneId.systemDefault())
            : LocalDateTime.now();

        // Try to get content from HTML first
        String texto = downloadHtmlContent(entry.getLink(), boeId);
        
        // If HTML content is empty, try PDF
        if (texto == null || texto.trim().isEmpty()) {
            texto = downloadPdfContent(pdfUrl, boeId);
        }

        if (texto == null || texto.trim().isEmpty()) {
            logger.warn("No content available for BOE ID: " + boeId);
            return;
        }

        // Extract fields using ConvocatoriaExtractor
        Map<String, Object> extractedFields = convocatoriaExtractor.extractFields(texto);
        
        // Create or update convocatoria
        saveConvocatoria(boeId, entry.getTitle(), texto, fechaPublicacion, 
                        entry.getLink(), extractedFields);
    }

    private String extractBoeId(String link) {
        Matcher matcher = BOE_ID_PATTERN.matcher(link);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String downloadHtmlContent(String link, String boeId) {
        try {
            Document doc = Jsoup.connect(link).get();
            Element textDiv = doc.getElementById("textoxslt");
            
            if (textDiv != null) {
                String text = textDiv.text();
                if (!text.isEmpty()) {
                    logger.info("Using HTML content for BOE ID: " + boeId);
                    return text;
                }
            }
        } catch (IOException e) {
            logger.error("Error downloading HTML content for BOE ID: " + boeId, e);
        }
        return null;
    }

    private String downloadPdfContent(String pdfUrl, String boeId) {
        try {
            URL url = URI.create(pdfUrl).toURL();
            byte[] pdfBytes = url.openStream().readAllBytes();
            
            // Check if it's a valid PDF
            if (pdfBytes.length > 4 && pdfBytes[0] == '%' && pdfBytes[1] == 'P' && 
                pdfBytes[2] == 'D' && pdfBytes[3] == 'F') {
                PDDocument document = Loader.loadPDF(pdfBytes);
                try {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String text = stripper.getText(document);
                    logger.info("Using PDF content for BOE ID: " + boeId);
                    return text;
                } finally {
                    document.close();
                }
            }
        } catch (IOException e) {
            logger.error("Error downloading PDF content for BOE ID: " + boeId, e);
        }
        return null;
    }

    private void saveConvocatoria(String boeId, String titulo, String texto, 
                                 LocalDateTime fechaPublicacion, String enlace, 
                                 Map<String, Object> datosExtra) {
        try {
            // Check if convocatoria already exists
            Convocatoria convocatoria = convocatoriaRepository.findByBoeId(boeId)
                .orElse(new Convocatoria());
            
            // Update fields
            convocatoria.setBoeId(boeId);
            convocatoria.setTitulo(titulo);
            convocatoria.setTexto(texto);
            convocatoria.setFechaPublicacion(fechaPublicacion);
            convocatoria.setEnlace(enlace);
            
            // Convert datosExtra to JSON string
            String datosExtraJson = objectMapper.writeValueAsString(datosExtra);
            convocatoria.setDatosExtra(datosExtraJson);
            
            // Save using ConvocatoriaService to trigger tagging
            convocatoriaService.saveOrUpdate(convocatoria);
            
            logger.info("Successfully saved convocatoria with BOE ID: " + boeId);
        } catch (Exception e) {
            logger.error("Error saving convocatoria with BOE ID: " + boeId, e);
        }
    }
}