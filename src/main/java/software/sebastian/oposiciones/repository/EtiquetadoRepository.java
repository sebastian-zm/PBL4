package software.sebastian.oposiciones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import software.sebastian.oposiciones.model.Etiquetado;

/**
 * Repositorio para la entidad ETIQUETADO.
 * Proporciona métodos de acceso a la tabla ETIQUETADO.
 */
@Repository
public interface EtiquetadoRepository extends JpaRepository<Etiquetado, Etiquetado.PrimaryKey> {

    /**
     * Obtiene todas las filas de ETIQUETADO para un modelo dado.
     * 
     * @param modeloId El ID del modelo de embedding.
     * @return Lista de Etiquetado con ese modeloId.
     */
    List<Etiquetado> findByModeloId(Integer modeloId);
}
