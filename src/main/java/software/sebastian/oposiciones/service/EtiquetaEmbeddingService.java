package software.sebastian.oposiciones.service;

import java.util.IllegalFormatException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.openai.client.OpenAIClient;
import software.sebastian.oposiciones.model.Etiqueta;
import software.sebastian.oposiciones.repository.EtiquetaRepository;
import software.sebastian.oposiciones.repository.ModeloEmbeddingRepository;
import software.sebastian.oposiciones.repository.ModeloRepository;

/**
 * Servicio para generar embeddings de Etiqueta extrayendo toda la lógica común a EmbeddingService.
 */
@Service
public class EtiquetaEmbeddingService extends ModeloEmbeddingService<Etiqueta> {

  private final EtiquetaRepository etiquetaRepo;

  public EtiquetaEmbeddingService(OpenAIClient openAIClient,
                                  ModeloRepository modeloRepo,
                                  ModeloEmbeddingRepository modeloEmbeddingRepo,
                                  EtiquetaRepository etiquetaRepo) {
    super(openAIClient, modeloRepo, modeloEmbeddingRepo);
    this.etiquetaRepo = etiquetaRepo;
  }

  @Override
  public Etiqueta loadEntity(Integer id) {
    return etiquetaRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Etiqueta no encontrada: " + id));
  }

  @Override
  public Integer getEntityId(Etiqueta etiqueta) {
    return etiqueta.getEtiquetaId();
  }

  @Override
  public LocalDateTime getEntityUpdatedAt(Etiqueta etiqueta) {
    return etiqueta.getUpdatedAt();
  }

  public String entityType = "ETIQUETA";

  @Override
  public String buildInput(Etiqueta etiqueta) {
    String nombre = etiqueta.getNombre() != null ? etiqueta.getNombre() : "";
    String texto;
    if (etiqueta.getFormato() != null && !etiqueta.getFormato().isBlank()) {
      try {
        texto = String.format(etiqueta.getFormato(), nombre);
      } catch (IllegalFormatException e) {
        texto = nombre;
      }
    } else {
      texto = nombre;
    }
    return texto.trim();
  }
}
