package software.sebastian.oposiciones.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.sebastian.oposiciones.model.Convocatoria;
import software.sebastian.oposiciones.repository.ConvocatoriaRepository;

@Service
public class ConvocatoriaService {

    private final ConvocatoriaRepository convocatoriaRepo;
    private final TaggingService taggingService;

    public ConvocatoriaService(
            ConvocatoriaRepository convocatoriaRepo,
            TaggingService taggingService) {
        this.convocatoriaRepo = convocatoriaRepo;
        this.taggingService = taggingService;
    }

    /**
     * Recupera todas las convocatorias.
     */
    public List<Convocatoria> findAll() {
        return convocatoriaRepo.findAll();
    }

    /**
     * Guarda o actualiza una convocatoria y dispara el etiquetado asíncrono.
     */
    @Transactional
    public Convocatoria saveOrUpdate(Convocatoria convocatoria) {
        Convocatoria saved = convocatoriaRepo.save(convocatoria);
        // Etiquetado en background, no bloquea la petición
        taggingService.tagConvocatoriaAsync(saved.getConvocatoriaId());
        return saved;
    }

    /**
     * Recupera una convocatoria por ID.
     */
    public Convocatoria findById(Integer id) {
        return convocatoriaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Convocatoria no encontrada: " + id));
    }

    /**
     * Elimina una convocatoria por ID.
     */
    @Transactional
    public void delete(Integer id) {
        convocatoriaRepo.deleteById(id);
    }
}
