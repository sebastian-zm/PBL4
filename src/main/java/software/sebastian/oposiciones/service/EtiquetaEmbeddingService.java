package software.sebastian.oposiciones.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.openai.client.OpenAIClient;
import com.openai.models.embeddings.EmbeddingCreateParams;
import software.sebastian.oposiciones.model.Etiqueta;
import software.sebastian.oposiciones.model.ModeloEmbedding;
import software.sebastian.oposiciones.repository.EtiquetaRepository;
import software.sebastian.oposiciones.repository.ModeloEmbeddingRepository;
import software.sebastian.oposiciones.repository.ModeloRepository;

@Service
public class EtiquetaEmbeddingService {

  private final OpenAIClient openAIClient;
  private final ModeloRepository modeloRepo;
  private final ModeloEmbeddingRepository modeloEmbeddingRepo;
  private final EtiquetaRepository etiquetaRepo;

  private static final String EMBEDDING_MODEL = "text-embedding-3-large";

  public EtiquetaEmbeddingService(OpenAIClient openAIClient,
                                 ModeloRepository modeloRepo,
                                 ModeloEmbeddingRepository modeloEmbeddingRepo,
                                 EtiquetaRepository etiquetaRepo) {
    this.openAIClient = openAIClient;
    this.modeloRepo = modeloRepo;
    this.modeloEmbeddingRepo = modeloEmbeddingRepo;
    this.etiquetaRepo = etiquetaRepo;
  }

  @Transactional
  public void generarYGuardarEmbedding(Integer etiquetaId) {
    Etiqueta etiqueta = etiquetaRepo.findById(etiquetaId)
        .orElseThrow(() -> new IllegalArgumentException("Etiqueta no encontrada: " + etiquetaId));

    String texto = (etiqueta.getNombre() != null ? etiqueta.getNombre() : "") + 
                   " " + 
                   (etiqueta.getDescripcion() != null ? etiqueta.getDescripcion() : "");
    texto = texto.trim();

    if (texto.isEmpty()) {
      // No hacer nada si no hay texto
      return;
    }

    // Obtener modeloId del modelo de embedding
    Integer modeloId = modeloRepo.findByNombre(EMBEDDING_MODEL)
        .orElseThrow(() -> new IllegalStateException("Modelo embedding no encontrado: " + EMBEDDING_MODEL))
        .getModeloId();

    // Llamar a OpenAI embeddings API
    List<Double> embedding = openAIClient.embeddings().create(
        EmbeddingCreateParams.builder()
            .model(EMBEDDING_MODEL)
            .input(texto)
            .build()
    ).data().get(0).embedding();

    // Convertir List<Double> a byte[], double[] o similar según modeloEmbedding entity

    // Aquí asumiendo que ModeloEmbedding usa double[] para embedding campo
    // Crear y guardar modeloEmbedding
    ModeloEmbedding me = new ModeloEmbedding();
    me.setModeloId(modeloId);
    me.setEtiquetaId(etiquetaId);

    // Supongamos que tienes setter para double[] en ModeloEmbedding (según entidad)
    // Si usas byte[] o JSON, adaptar la conversión.

    // Ejemplo: convertir List<Double> a double[]
    double[] embeddingArray = new double[embedding.size()];
    for (int i=0; i<embedding.size(); i++) {
      embeddingArray[i] = embedding.get(i);
    }
    me.setEmbedding(embeddingArray);

    modeloEmbeddingRepo.save(me);
  }
}
