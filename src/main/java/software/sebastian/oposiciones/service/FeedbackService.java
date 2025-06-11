package software.sebastian.oposiciones.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import software.sebastian.oposiciones.model.Feedback;
import software.sebastian.oposiciones.model.FeedbackId;
import software.sebastian.oposiciones.repository.FeedbackRepository;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository repo;

    @Transactional
    public void upsert(int usuarioId, int convocatoriaId, int etiquetaId, boolean aprobado) {
        FeedbackId id = new FeedbackId(usuarioId, convocatoriaId, etiquetaId);
        Optional<Feedback> opt = repo.findById(id);

            // Si ya existe feedback, NO dejamos modificarlo
        if (repo.existsById(id)) {
            throw new IllegalStateException("Ya has evaluado este etiquetado.");
        }

        Feedback fb = opt.orElseGet(() -> {
            Feedback nuevo = new Feedback();
            nuevo.setUsuarioId(usuarioId);
            nuevo.setConvocatoriaId(convocatoriaId);
            nuevo.setEtiquetaId(etiquetaId);
            nuevo.setFecha(LocalDateTime.now()); // si necesitas setearlo al momento
            nuevo.setCreatedAt(LocalDateTime.now());
            return nuevo;
        });

        fb.setAprobado(aprobado);
        fb.setUpdatedAt(LocalDateTime.now());

        repo.save(fb);
        // Los triggers se encargan de actualizar ETIQUETADO_GLOBAL
    }

    public boolean existeFeedback(int usuarioId, int convocatoriaId, int etiquetaId) {
        FeedbackId id = new FeedbackId(usuarioId, convocatoriaId, etiquetaId);
        return repo.existsById(id);
    }
}
