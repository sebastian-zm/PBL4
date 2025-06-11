package software.sebastian.oposiciones.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import software.sebastian.oposiciones.model.Feedback;
import software.sebastian.oposiciones.model.FeedbackId;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, FeedbackId> {
    
    // Puedes agregar métodos personalizados si los necesitas, por ejemplo:

    // Listar todos los feedbacks de un usuario
    List<Feedback> findByUsuarioId(Integer usuarioId);

    // Buscar feedbacks por convocatoria y etiqueta
    List<Feedback> findByConvocatoriaIdAndEtiquetaId(Integer convocatoriaId, Integer etiquetaId);

    // Verificar si existe feedback aprobado para un usuario en una convocatoria
    boolean existsByUsuarioIdAndConvocatoriaIdAndAprobadoTrue(Integer usuarioId, Integer convocatoriaId);
}