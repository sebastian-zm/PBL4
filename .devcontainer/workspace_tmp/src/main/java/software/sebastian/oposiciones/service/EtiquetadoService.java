package software.sebastian.oposiciones.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import software.sebastian.oposiciones.model.Etiquetado;
import software.sebastian.oposiciones.repository.EtiquetadoRepository;
import software.sebastian.oposiciones.repository.ModeloRepository;


@Service
public class EtiquetadoService {

    private static final String EMBEDDING_MODEL = "text-embedding-3-large";
    private static final double UMBRAL = 0.4;

    private final EtiquetadoRepository etiquetadoRepo;
    private final ModeloRepository modeloRepo;
    private final EtiquetaEmbeddingService etiquetaEmbeddingService;
    private final ConvocatoriaEmbeddingService convocatoriaEmbeddingService;

    public EtiquetadoService(EtiquetadoRepository etiquetadoRepo,
                          ModeloRepository modeloRepo,
                          EtiquetaEmbeddingService etiquetaEmbeddingService,
                          ConvocatoriaEmbeddingService convocatoriaEmbeddingService) {
        this.etiquetadoRepo = etiquetadoRepo;
        this.modeloRepo = modeloRepo;
        this.convocatoriaEmbeddingService = convocatoriaEmbeddingService;
        this.etiquetaEmbeddingService = etiquetaEmbeddingService;
    }

    @Async("taggingExecutor")
    @Transactional
    public CompletableFuture<Void> tagConvocatoriaAsync(Integer convocatoriaId) {
        tagConvocatoria(convocatoriaId);
        return CompletableFuture.completedFuture(null);
    }

    private void tagConvocatoria(Integer convocatoriaId) {
        try {
            // Obtener el embedding de la convocatoria usando el servicio especializado
            double[] embConv = convocatoriaEmbeddingService.generarYGuardarEmbedding(convocatoriaId);
    
            // Averiguar el modeloId en la tabla MODELO
            Integer modeloId = modeloRepo.findByNombre(EMBEDDING_MODEL)
                .orElseThrow(() -> new IllegalStateException("Modelo no encontrado"))
                .getModeloId();

            // Cargar todos los embeddings de etiquetas para ese modelo
            Map<Integer,double[]> embeddingsPorEtiqueta =
                etiquetaEmbeddingService.cargarEmbeddingsPorModelo(modeloId);

            // Por cada etiqueta, calcular similitud y persistir en ETIQUETADO si supera umbral
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
           
        } catch (Exception e) {
            throw new RuntimeException("Error en tagConvocatoria: " + e.getMessage(), e);
        }
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
