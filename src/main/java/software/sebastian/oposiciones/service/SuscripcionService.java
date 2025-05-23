package software.sebastian.oposiciones.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.sebastian.oposiciones.model.Suscripcion;
import software.sebastian.oposiciones.model.SuscripcionEtiqueta;
import software.sebastian.oposiciones.repository.SuscripcionRepository;
import software.sebastian.oposiciones.repository.SuscripcionEtiquetaRepository;


@Service
public class SuscripcionService {

    private final SuscripcionRepository suscripcionRepo;

    private final SuscripcionEtiquetaRepository susetiRepo;

    public SuscripcionService(SuscripcionRepository suscripcionRepo,
            SuscripcionEtiquetaRepository susetiRepo) {

        this.suscripcionRepo = suscripcionRepo;
        this.susetiRepo = susetiRepo;
    }


    public List<Suscripcion> findAll() {
        return suscripcionRepo.findAll();
    }

    public List<Suscripcion> findSusByUser(Integer usuarioID) {
        return suscripcionRepo.findByUsuarioId(usuarioID);
    }


    @Transactional
    public Suscripcion saveOrUpdate(Suscripcion suscripcion) {
        Suscripcion saved = suscripcionRepo.save(suscripcion);
        return saved;
    }


    public Suscripcion findById(Integer id) {
        return suscripcionRepo.findById(id).orElseThrow( () -> new IllegalArgumentException("Subscripcion no encontrada: " + id));
    }

    @Transactional
    public void delete(Integer id) {
        suscripcionRepo.deleteById(id);
    }

    @Transactional
    public Suscripcion create(List<Integer> lista, Integer id) {
        Suscripcion s = new Suscripcion();
        s.setUsuarioId(id);
        Suscripcion sSaved = suscripcionRepo.save(s);

        for (Integer etiquetaId : lista) {
            SuscripcionEtiqueta se =
                    new SuscripcionEtiqueta(sSaved.getSuscripcionId(), etiquetaId);

            susetiRepo.save(se);
        }
        return sSaved;
    }


}
