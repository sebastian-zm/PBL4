// src/main/java/software/sebastian/oposiciones/repository/ModeloEmbeddingRepository.java
package software.sebastian.oposiciones.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import software.sebastian.oposiciones.model.ModeloEmbedding;

@Repository
public interface ModeloEmbeddingRepository
        extends JpaRepository<ModeloEmbedding, ModeloEmbedding.PrimaryKey> {
    List<ModeloEmbedding> findByModeloId(Integer modeloId);

    List<ModeloEmbedding> findByModeloIdAndEntidadTipo(Integer modeloId, String entidadTipo);
}
