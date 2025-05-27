package software.sebastian.oposiciones.service;


import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.sebastian.oposiciones.model.ArbolEtiqueta;
import software.sebastian.oposiciones.model.Etiqueta;
import software.sebastian.oposiciones.model.SuscripcionEtiqueta;
import software.sebastian.oposiciones.repository.EtiquetaRepository;
import software.sebastian.oposiciones.repository.SuscripcionRepository;
import software.sebastian.oposiciones.repository.SuscripcionEtiquetaRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

@Service
public class SuscripcionEtiquetaService {

    private final SuscripcionEtiquetaRepository suscripcion_etiquetaRepo;
    private final EtiquetaRepository etiquetaRepo;
    private final SuscripcionRepository susRepo;

    public SuscripcionEtiquetaService(SuscripcionRepository susRepo,
            SuscripcionEtiquetaRepository suscripcion_etiquetaRepo,
            EtiquetaRepository etiquetaRepo) {
        this.suscripcion_etiquetaRepo = suscripcion_etiquetaRepo;
        this.etiquetaRepo = etiquetaRepo;
        this.susRepo = susRepo;
    }

    public Map<Integer, List<Etiqueta>> getEtiquetasPorSuscripcion() {
        List<SuscripcionEtiqueta> relaciones = suscripcion_etiquetaRepo.findAll();
        List<Etiqueta> todasLasEtiquetas = etiquetaRepo.findAll();
        Map<Integer, Etiqueta> etiquetaPorId = new HashMap<>();

        for (Etiqueta etiqueta : todasLasEtiquetas) {
            etiquetaPorId.put(etiqueta.getEtiquetaId(), etiqueta);
        }

        Map<Integer, List<Etiqueta>> resultado = new HashMap<>();
        for (SuscripcionEtiqueta relacion : relaciones) {
            int suscripcionId = relacion.getSuscripcionId();
            Etiqueta etiqueta = etiquetaPorId.get(relacion.getEtiquetaId());

            if (etiqueta != null) {
                resultado.computeIfAbsent(suscripcionId, k -> new ArrayList<>()).add(etiqueta);
            }
        }

        return resultado;
    }

    public List<Integer> getEtiquetaIdsPorSuscripcion(Integer suscripcionId) {
        return suscripcion_etiquetaRepo.findBySuscripcionId(suscripcionId).stream()
                .map(SuscripcionEtiqueta::getEtiquetaId).collect(Collectors.toList());
    }

    public Map<Integer, Set<Integer>> mapaDescendientesPorEtiqueta(List<ArbolEtiqueta> relaciones) {
        Map<Integer, Set<Integer>> descendientes = new HashMap<>();

        for (ArbolEtiqueta relacion : relaciones) {
            int ancestro = relacion.getAncestro().getEtiquetaId();
            int descendiente = relacion.getDescendiente().getEtiquetaId();
            descendientes.computeIfAbsent(ancestro, k -> new HashSet<>()).add(descendiente);
        }
        Set<Integer> todosLosIds = new HashSet<>();
        for (ArbolEtiqueta rel : relaciones) {
            todosLosIds.add(rel.getAncestro().getEtiquetaId());
            todosLosIds.add(rel.getDescendiente().getEtiquetaId());
        }
        for (Integer id : todosLosIds) {
            descendientes.computeIfAbsent(id, k -> new HashSet<>()).add(id);
        }

        return descendientes;
    }

    @Transactional
    public void deleteEtiquetasPorSuscripcion(Integer id) {
        suscripcion_etiquetaRepo.deleteBySuscripcionId(id);
    }

    @Transactional

    public void update(List<Integer> lista, Integer id, String nombre) {
        susRepo.findById(id).ifPresent(suscripcion -> {
            suscripcion.setUpdatedAt(LocalDateTime.now());
            suscripcion.setNombre(nombre);
            susRepo.save(suscripcion);
        });
        suscripcion_etiquetaRepo.deleteBySuscripcionId(id);
        for (Integer etiquetaId : lista) {
            SuscripcionEtiqueta se = new SuscripcionEtiqueta(id, etiquetaId);
            suscripcion_etiquetaRepo.save(se);
        }
    }
}
