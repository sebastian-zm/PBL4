// src/main/java/software/sebastian/oposiciones/service/ModeloEmbeddingService.java
package software.sebastian.oposiciones.service;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import software.sebastian.oposiciones.model.ModeloEmbedding;
import software.sebastian.oposiciones.repository.ModeloEmbeddingRepository;

@Service
public class ModeloEmbeddingService {

    private final ModeloEmbeddingRepository repo;

    public ModeloEmbeddingService(ModeloEmbeddingRepository repo) {
        this.repo = repo;
    }

    /**
     * Carga todos los embeddings para un modelo dado y los devuelve
     * en un Map<etiquetaId, vectorEmbedding>.
     */
    public Map<Integer,double[]> cargarEmbeddingsPorModelo(int modeloId) {
        return repo.findByModeloId(modeloId).stream()
                   .collect(Collectors.toMap(
                       ModeloEmbedding::getEtiquetaId,
                       ModeloEmbedding::getEmbedding
                   ));
    }
}
