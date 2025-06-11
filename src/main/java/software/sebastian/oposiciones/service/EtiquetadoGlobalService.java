// src/main/java/software/sebastian/oposiciones/service/EtiquetadoGlobalService.java
package software.sebastian.oposiciones.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import software.sebastian.oposiciones.model.EtiquetadoGlobal;
import software.sebastian.oposiciones.model.EtiquetadoGlobal.PrimaryKey;
import software.sebastian.oposiciones.repository.EtiquetadoGlobalRepository;

@Service
@Transactional
public class EtiquetadoGlobalService {

    private final EtiquetadoGlobalRepository repo;

    public EtiquetadoGlobalService(EtiquetadoGlobalRepository repo) {
        this.repo = repo;
    }

    /** Devuelve todos los registros de EtiquetadoGlobal global */
    public List<EtiquetadoGlobal> findAll() {
        return repo.findAll();
    }

    /** Busca por PK (convocatoriaId + etiquetaId) */
    public Optional<EtiquetadoGlobal> findById(Integer convocatoriaId, Integer etiquetaId) {
        return repo.findById(new PrimaryKey(convocatoriaId, etiquetaId));
    }

    /** Inserta o actualiza un registro de EtiquetadoGlobal */
    public EtiquetadoGlobal saveOrUpdate(EtiquetadoGlobal e) {
        return repo.save(e);
    }

    /** Borra un registro */
    public void delete(Integer convocatoriaId, Integer etiquetaId) {
        repo.deleteById(new PrimaryKey(convocatoriaId, etiquetaId));
    }
}
