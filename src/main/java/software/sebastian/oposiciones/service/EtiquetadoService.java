package software.sebastian.oposiciones.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.sebastian.oposiciones.model.Etiqueta;
import software.sebastian.oposiciones.model.Etiquetado;
import software.sebastian.oposiciones.repository.ArbolEtiquetaRepository;
import software.sebastian.oposiciones.repository.EtiquetaRepository;
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
    private final EtiquetaRepository etiRepo;
    private final SuscripcionEtiquetaService SEservice;

    private final ArbolEtiquetaRepository arbolRepo;
    public EtiquetadoService(EtiquetadoRepository etiquetadoRepo, ModeloRepository modeloRepo,
            EtiquetaEmbeddingService etiquetaEmbeddingService,
            ConvocatoriaEmbeddingService convocatoriaEmbeddingService, EtiquetaRepository etiRepo, SuscripcionEtiquetaService SEservice,ArbolEtiquetaRepository arbolRepo) {
        this.etiquetadoRepo = etiquetadoRepo;
        this.modeloRepo = modeloRepo;
        this.convocatoriaEmbeddingService = convocatoriaEmbeddingService;
        this.etiquetaEmbeddingService = etiquetaEmbeddingService;
        this.etiRepo=etiRepo;
        this.SEservice=SEservice;
        this.arbolRepo=arbolRepo;
    }

    @Async("taggingExecutor")
    @Transactional
    public CompletableFuture<Void> tagConvocatoriaAsync(Integer convocatoriaId) {
        tagConvocatoria(convocatoriaId);
        return CompletableFuture.completedFuture(null);
    }


public List<Integer> convocatoriasDeEtiquetas(List<Integer> etiquetasSuscripcion) {
    Map<Integer, Set<Integer>> etiquetasPorConvocatoria = new HashMap<>();
    Map<Integer, Set<Integer>> descendientes = SEservice.mapaDescendientesPorEtiqueta(arbolRepo.findAll());

    // Paso A: Agrupa etiquetas de convocatorias
    for (Etiquetado r : etiquetadoRepo.findAll()) {
        etiquetasPorConvocatoria
            .computeIfAbsent(r.getConvocatoriaId(), k -> new HashSet<>())
            .add(r.getEtiquetaId());
    }

    List<Integer> resultado = new ArrayList<>();

    for (Map.Entry<Integer, Set<Integer>> entry : etiquetasPorConvocatoria.entrySet()) {
        Set<Integer> etiquetasConvocatoria = entry.getValue();

        boolean cumple = true;
        for (Integer etiquetaSuscripcion : etiquetasSuscripcion) {
            Set<Integer> descendientesDeEtiqueta = descendientes.getOrDefault(etiquetaSuscripcion, Set.of(etiquetaSuscripcion));

            // Verifica si la convocatoria tiene al menos una etiqueta descendiente de esta etiqueta de la suscripción
            boolean tieneAlguna = false;
            for (Integer e : etiquetasConvocatoria) {
                if (descendientesDeEtiqueta.contains(e)) {
                    tieneAlguna = true;
                    break;
                }
            }

            if (!tieneAlguna) {
                cumple = false;
                break;
            }
        }

        if (cumple) {
            resultado.add(entry.getKey());
        }
    }

    return resultado;
}



    public Map<Integer, List<Etiqueta>> getEtiquetasPorConvocatoria() {
        List<Etiquetado> relaciones = etiquetadoRepo.findAll();
        List<Etiqueta> todasLasEtiquetas = etiRepo.findAll();
        Map<Integer, Etiqueta> etiquetaPorId = new HashMap<>();

        for (Etiqueta etiqueta : todasLasEtiquetas) {
            etiquetaPorId.put(etiqueta.getEtiquetaId(), etiqueta);
        }

        Map<Integer, List<Etiqueta>> resultado = new HashMap<>();
        for (Etiquetado relacion : relaciones) {
            int suscripcionId = relacion.getConvocatoriaId();
            Etiqueta etiqueta = etiquetaPorId.get(relacion.getEtiquetaId());

            if (etiqueta != null) {
                resultado.computeIfAbsent(suscripcionId, k -> new ArrayList<>()).add(etiqueta);
            }
        }

        return resultado;
    }

    private void tagConvocatoria(Integer convocatoriaId) {
        try {
            // Obtener el embedding de la convocatoria usando el servicio especializado
            double[] embConv =
                    convocatoriaEmbeddingService.generarYGuardarEmbedding(convocatoriaId);

            // Averiguar el modeloId en la tabla MODELO
            Integer modeloId = modeloRepo.findByNombre(EMBEDDING_MODEL)
                    .orElseThrow(() -> new IllegalStateException("Modelo no encontrado"))
                    .getModeloId();

            // Cargar todos los embeddings de etiquetas para ese modelo
            Map<Integer, double[]> embeddingsPorEtiqueta =
                    etiquetaEmbeddingService.cargarEmbeddingsPorModelo(modeloId);

            // Por cada etiqueta, calcular similitud y persistir en ETIQUETADO si supera umbral
            embeddingsPorEtiqueta.forEach((etiquetaId, embLabel) -> {
                double sim = cosineSimilarity(embConv, embLabel);
                if (sim >= UMBRAL) {

                    Etiquetado et = new Etiquetado(convocatoriaId, etiquetaId, modeloId, null, // valoración
                                                                                               // (puede
                                                                                               // quedar
                                                                                               // a
                                                                                               // null)
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
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
