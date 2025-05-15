package software.sebastian.oposiciones.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;
import com.openai.client.OpenAIClient;
import com.openai.models.embeddings.EmbeddingCreateParams;
import software.sebastian.oposiciones.model.ModeloEmbedding;
import software.sebastian.oposiciones.repository.ModeloRepository;
import software.sebastian.oposiciones.repository.ModeloEmbeddingRepository;


public abstract class ModeloEmbeddingService<E> {

  private final OpenAIClient openAIClient;
  private final ModeloRepository modeloRepo;
  private final ModeloEmbeddingRepository modeloEmbeddingRepo;

  public ModeloEmbeddingService(OpenAIClient openAIClient,
                                      ModeloRepository modeloRepo,
                                      ModeloEmbeddingRepository modeloEmbeddingRepo) {
    this.openAIClient = openAIClient;
    this.modeloRepo = modeloRepo;
    this.modeloEmbeddingRepo = modeloEmbeddingRepo;
  }

  String EMBEDDING_MODEL = "text-embedding-3-large";

  /** Carga la entidad por su ID, o lanza excepción si no existe */
  abstract E loadEntity(Integer id);
  
  /** ID numérico de la entidad */
  abstract Integer getEntityId(E entity);

  /** Obtiene la fecha de última actualización de la entidad */
  abstract LocalDateTime getEntityUpdatedAt(E entity);

  /** Construye el texto de entrada para el modelo de embedding */
  abstract String buildInput(E entity);

  /** Retorna el valor de entidadTipo para el embedding (ej. "ETIQUETA") */
  String entityType;


  @Transactional
  double[] generarYGuardarEmbedding(Integer id) {
    E entity = loadEntity(id);
    String input = buildInput(entity).trim();
    if (input == null || input.isEmpty()) {
      throw new IllegalStateException("Sin input para el embedding: " + entityType + id);
    }

    // Obtener modeloId
    Integer modeloId = modeloRepo.findByNombre(EMBEDDING_MODEL)
        .orElseThrow(() -> new IllegalStateException("Modelo embedding no encontrado: " + EMBEDDING_MODEL))
        .getModeloId();

    // Comprobar existente
    Integer entidadId = getEntityId(entity);
    Optional<ModeloEmbedding> existente = modeloEmbeddingRepo.findById(
        new ModeloEmbedding.PrimaryKey(modeloId, entidadId, entityType));
    if (existente.isPresent()) {
      LocalDateTime embUp = existente.get().getUpdatedAt();
      LocalDateTime entUp = getEntityUpdatedAt(entity);
      if (!embUp.isBefore(entUp)) {
        return existente.get().getEmbedding();
      }
    }

    // Generar embedding
    List<Double> embeddingList = openAIClient.embeddings().create(
        EmbeddingCreateParams.builder()
            .model(EMBEDDING_MODEL)
            .input(input)
            .build()
    ).data().get(0).embedding();

    double[] array = new double[embeddingList.size()];
    for (int i = 0; i < embeddingList.size(); i++) {
      array[i] = embeddingList.get(i);
    }

    // Guardar
    ModeloEmbedding me = new ModeloEmbedding(modeloId, entidadId, entityType, array);
    modeloEmbeddingRepo.save(me);

    return array;
  }

      /**
     * Carga todos los embeddings para un modelo dado y los devuelve
     * en un Map<etiquetaId, vectorEmbedding>.
     */
    public Map<Integer,double[]> cargarEmbeddingsPorModelo(int modeloId) {
        return modeloEmbeddingRepo.findByModeloIdAndEntidadTipo(modeloId, entityType).stream()
                   .collect(Collectors.toMap(
                       ModeloEmbedding::getEntidadId,
                       ModeloEmbedding::getEmbedding
                   ));
    }
}