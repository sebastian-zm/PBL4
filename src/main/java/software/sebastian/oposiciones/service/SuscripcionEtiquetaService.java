package software.sebastian.oposiciones.service;


import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.sebastian.oposiciones.model.Etiqueta;
import software.sebastian.oposiciones.model.SuscripcionEtiqueta;
import software.sebastian.oposiciones.repository.EtiquetaRepository;
import software.sebastian.oposiciones.repository.SuscripcionRepository;
import software.sebastian.oposiciones.repository.SuscripcionEtiquetaRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


    // public List<Etiqueta> getEtiquetasDeSuscripcion(Integer suscripcionId) {
    // List<Suscripcion_Etiqueta> relaciones =
    // suscripcion_etiquetaRepo.findBySuscripcionId(suscripcionId);
    // List<Etiqueta> etiquetas = new ArrayList<>();
    // for (Suscripcion_Etiqueta relacion : relaciones) {
    // etiquetaRepo.findById(relacion.getEtiquetaId()).ifPresent(etiquetas::add);
    // }
    // return etiquetas;
    // }

    public List<Integer> getEtiquetaIdsPorSuscripcion(Integer suscripcionId) {
        return suscripcion_etiquetaRepo.findBySuscripcionId(suscripcionId).stream()
                .map(SuscripcionEtiqueta::getEtiquetaId).collect(Collectors.toList());
    }


    @Transactional
    public void deleteEtiquetasPorSuscripcion(Integer id) {
        suscripcion_etiquetaRepo.deleteBySuscripcionId(id);
    }

    @Transactional
    public void update(List<Integer> lista, Integer id) {
        susRepo.findById(id).ifPresent(suscripcion -> {
            suscripcion.setUpdatedAt(LocalDateTime.now());
            susRepo.save(suscripcion);
        });
        suscripcion_etiquetaRepo.deleteBySuscripcionId(id);
        for (Integer etiquetaId : lista) {
            SuscripcionEtiqueta se = new SuscripcionEtiqueta(id, etiquetaId);
            suscripcion_etiquetaRepo.save(se);
        }

    }


}
