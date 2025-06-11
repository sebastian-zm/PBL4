package software.sebastian.oposiciones.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.sebastian.oposiciones.model.Convocatoria;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ConvocatoriaGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ConvocatoriaGenerator.class);
    private static final String RSS_URL = "https://www.boe.es/rss/canal_per.php?l=p&c=140";
    private static final Pattern BOE_ID_PATTERN = Pattern.compile("id=(BOE-A-\\d{4}-\\d+)");

    private final ConvocatoriaService convocatoriaService;
    private final ConvocatoriaExtractor extractor;
    private final ObjectMapper objectMapper;

    @Autowired
    public ConvocatoriaGenerator(ConvocatoriaService convocatoriaService, 
                                ConvocatoriaExtractor extractor,
                                ObjectMapper objectMapper) {
        this.convocatoriaService = convocatoriaService;
        this.extractor = extractor;
        this.objectMapper = objectMapper;
    }

    public void processRssFeed() {
        logger.info("Starting RSS feed processing from BOE...");
        
        try {
            // Use HttpClient to fetch RSS content
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RSS_URL))
                    .GET()
                    .build();
            
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            
            if (response.statusCode() == 200) {
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(response.body()));
                
                for (SyndEntry entry : feed.getEntries()) {
                    try {
                        processEntry(entry);
                    } catch (Exception e) {
                        logger.error("Error processing entry: " + entry.getLink(), e);
                    }
                }
            } else {
                logger.error("Failed to fetch RSS feed. Status code: " + response.statusCode());
            }
            
            logger.info("RSS feed processing completed.");
        } catch (Exception e) {
            logger.error("Error reading RSS feed", e);
        }
    }

    private void processEntry(SyndEntry entry) {
        String boeId = extractBoeId(entry.getLink());
        if (boeId == null) {
            logger.warn("Could not extract BOE ID from link: " + entry.getLink());
            return;
        }

        logger.info("Processing BOE ID: " + boeId);

        // Extract text content
        String texto = extractContent(entry);
        if (texto == null || texto.trim().isEmpty()) {
            logger.warn("No content found for BOE ID: " + boeId);
            return;
        }

        // Extract fields using the extractor
        Map<String, Object> extractedFields = extractor.extractFields(texto);
        
        // Create or update convocatoria
        Convocatoria convocatoria = new Convocatoria();
        convocatoria.setBoeId(boeId);
        convocatoria.setTitulo(entry.getTitle());
        convocatoria.setTexto(texto);
        convocatoria.setFechaPublicacion(convertToLocalDateTime(entry.getPublishedDate()));
        convocatoria.setEnlace(entry.getLink());
        
        try {
            String datosExtraJson = objectMapper.writeValueAsString(extractedFields);
            convocatoria.setDatosExtra(datosExtraJson);
        } catch (Exception e) {
            logger.error("Error serializing datosExtra", e);
        }

        convocatoriaService.saveOrUpdate(convocatoria);
        logger.info("Saved convocatoria: " + boeId);
    }

    private String extractBoeId(String link) {
        Matcher matcher = BOE_ID_PATTERN.matcher(link);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractContent(SyndEntry entry) {
        // First try to get HTML content
        String htmlContent = downloadHtml(entry.getLink());
        if (htmlContent != null && !htmlContent.trim().isEmpty()) {
            return htmlContent;
        }

        // Fallback to PDF if available
        if (entry.getUri() != null) {
            String pdfContent = downloadAndExtractPdf(entry.getUri());
            if (pdfContent != null && !pdfContent.trim().isEmpty()) {
                return pdfContent;
            }
        }

        return null;
    }

    private String downloadHtml(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Element textDiv = doc.getElementById("textoxslt");
            if (textDiv != null) {
                return textDiv.text();
            }
        } catch (IOException e) {
            logger.error("Error downloading HTML from: " + url, e);
        }
        return null;
    }

    private String downloadAndExtractPdf(String pdfUrl) {
        PDDocument document = null;
        try {
            // Use HttpClient to download PDF
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pdfUrl))
                    .GET()
                    .build();
            
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            
            if (response.statusCode() == 200) {
                document = org.apache.pdfbox.Loader.loadPDF(response.body());
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                return text;
            } else {
                logger.error("Failed to download PDF. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            logger.error("Error processing PDF from: " + pdfUrl, e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    logger.error("Error closing PDF document", e);
                }
            }
        }
        return null;
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return LocalDateTime.now();
        }
        return date.toInstant()
                   .atZone(ZoneId.systemDefault())
                   .toLocalDateTime();
    }
}