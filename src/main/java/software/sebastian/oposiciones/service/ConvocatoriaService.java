package software.sebastian.oposiciones.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.sebastian.oposiciones.model.Convocatoria;
import software.sebastian.oposiciones.model.Suscripcion;
import software.sebastian.oposiciones.repository.ConvocatoriaRepository;

@Service
public class ConvocatoriaService {

    private final ConvocatoriaRepository convocatoriaRepo;
    private final EtiquetadoService taggingService;
    private final SuscripcionEtiquetaService SEservice;
    private final SuscripcionService susservice;

    public ConvocatoriaService(ConvocatoriaRepository convocatoriaRepo,
            EtiquetadoService taggingService, SuscripcionEtiquetaService SEservice,
            SuscripcionService susservice) {
        this.convocatoriaRepo = convocatoriaRepo;
        this.taggingService = taggingService;
        this.SEservice = SEservice;
        this.susservice = susservice;
    }

    /**
     * Recupera todas las convocatorias.
     */
    public List<Convocatoria> findAll() {
        return convocatoriaRepo.findAll();
    }

    public List<Convocatoria> findConvocatoriasByTodasLasSuscripciones(Integer usuarioId) {
        Set<Convocatoria> convocatorias = new HashSet<>();

        // Obtener todas las suscripciones del usuario
        List<Suscripcion> suscripciones = susservice.findSusByUser(usuarioId);

        // Por cada suscripción, buscar sus convocatorias y agregarlas al set
        for (Suscripcion suscripcion : suscripciones) {
            List<Convocatoria> convocatoriasPorSuscripcion =
                    findConvocatoriasBySuscripcion(suscripcion.getSuscripcionId());
            convocatorias.addAll(convocatoriasPorSuscripcion);
        }

        // Convertir el set a una lista antes de devolver
        return new ArrayList<>(convocatorias);
    }



    public List<Convocatoria> findConvocatoriasBySuscripcion(Integer suscripcionId) {
        List<Integer> idDeEtiquetas;
        List<Integer> idDeConvocatorias;
        idDeEtiquetas = SEservice.getEtiquetaIdsPorSuscripcion(suscripcionId);
        idDeConvocatorias = taggingService.convocatoriasDeEtiquetas(idDeEtiquetas);
        return convocatoriaRepo.findAllById(idDeConvocatorias);
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

        return convocatoriaRepo.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Convocatoria no encontrada: " + id));
    }

    /**
     * Elimina una convocatoria por ID.
     */
    @Transactional
    public void delete(Integer id) {
        convocatoriaRepo.deleteById(id);
    }
}
