package software.sebastian.oposiciones.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import software.sebastian.oposiciones.model.Etiquetado;
import software.sebastian.oposiciones.model.Convocatoria;
import software.sebastian.oposiciones.repository.ConvocatoriaRepository;
import software.sebastian.oposiciones.repository.EtiquetadoRepository;
import software.sebastian.oposiciones.repository.ModeloRepository;

import com.openai.client.OpenAIClient;
import com.openai.models.embeddings.CreateEmbeddingResponse;
import com.openai.models.embeddings.EmbeddingCreateParams;

@Service
public class TaggingService {

    private static final String EMBEDDING_MODEL = "text-embedding-3-large";
    private static final double UMBRAL = 0.4;

    private final OpenAIClient openAIClient;
    private final ConvocatoriaRepository convocatoriaRepo;
    private final EtiquetadoRepository etiquetadoRepo;
    private final ModeloRepository modeloRepo;
    private final ModeloEmbeddingService modeloEmbeddingService;

    public TaggingService(OpenAIClient openAIClient,
                          ConvocatoriaRepository convocatoriaRepo,
                          EtiquetadoRepository etiquetadoRepo,
                          ModeloRepository modeloRepo,
                          ModeloEmbeddingService modeloEmbeddingService) {
        this.openAIClient = openAIClient;
        this.convocatoriaRepo = convocatoriaRepo;
        this.etiquetadoRepo = etiquetadoRepo;
        this.modeloRepo = modeloRepo;
        this.modeloEmbeddingService = modeloEmbeddingService;
    }

    @Async("taggingExecutor")
    @Transactional
    public CompletableFuture<Void> tagConvocatoriaAsync(Integer convocatoriaId) {
        tagConvocatoria(convocatoriaId);
        return CompletableFuture.completedFuture(null);
    }

    private void tagConvocatoria(Integer convocatoriaId) {
        // 1) Recuperar la convocatoria
        Convocatoria c = convocatoriaRepo.findById(convocatoriaId)
            .orElseThrow(() -> new IllegalArgumentException("No existe convocatoria " + convocatoriaId));

        // 2) Construir el texto y truncar
        String texto = c.getTitulo() + " " + c.getTexto();
        String truncated = texto.length() <= 10000 ? texto : texto.substring(0, 10000);

        // 3) Obtener el embedding de la convocatoria
        CreateEmbeddingResponse resp = openAIClient.embeddings().create(
            EmbeddingCreateParams.builder()
                .model(EMBEDDING_MODEL)
                .input(truncated)
                .build()
        );
        double[] embConv = resp.data().get(0).embedding()
                              .stream().mapToDouble(Double::doubleValue).toArray();

        // 4) Averiguar el modeloId en la tabla MODELO
        Integer modeloId = modeloRepo.findByNombre(EMBEDDING_MODEL)
            .orElseThrow(() -> new IllegalStateException("Modelo no encontrado"))
            .getModeloId();

        // 5) Cargar todos los embeddings de etiquetas para ese modelo
        Map<Integer,double[]> embeddingsPorEtiqueta =
            modeloEmbeddingService.cargarEmbeddingsPorModelo(modeloId);

        // 6) Por cada etiqueta, calcular similitud y persistir en ETIQUETADO si supera umbral
        embeddingsPorEtiqueta.forEach((etiquetaId, embLabel) -> {
            double sim = cosineSimilarity(embConv, embLabel);
            if (sim >= UMBRAL) {
                Etiquetado et = new Etiquetado(convocatoriaId,
                                               etiquetaId,
                                               modeloId,
                                               null,      // valoración (puede quedar a null)
                                               sim);
                etiquetadoRepo.save(et);
            }
        });
    }

    private double cosineSimilarity(double[] a, double[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot   += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
