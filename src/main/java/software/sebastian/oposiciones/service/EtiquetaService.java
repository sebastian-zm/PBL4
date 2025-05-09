package software.sebastian.oposiciones.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.sebastian.oposiciones.model.ArbolEtiqueta;
import software.sebastian.oposiciones.model.Etiqueta;
import software.sebastian.oposiciones.repository.ArbolEtiquetaRepository;
import software.sebastian.oposiciones.repository.EtiquetaRepository;

@Service
public class EtiquetaService {
    private final EtiquetaRepository etiquetaRepo;
    private final ArbolEtiquetaRepository arbolRepo;

    public EtiquetaService(EtiquetaRepository er, ArbolEtiquetaRepository ar) {
        this.etiquetaRepo = er;
        this.arbolRepo = ar;
    }

    public List<Etiqueta> findAll() {
        return etiquetaRepo.findAll();
    }

    public Etiqueta create(String nombre, String descripcion) {
        Etiqueta e = new Etiqueta();
        e.setNombre(nombre);
        e.setDescripcion(descripcion);
        return etiquetaRepo.save(e);
    }

    public Etiqueta update(Integer id, String nombre, String descripcion) {
        Etiqueta e = etiquetaRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No existe: " + id));
        e.setNombre(nombre);
        e.setDescripcion(descripcion);
        return etiquetaRepo.save(e);
    }

    @Transactional
    public void delete(Integer id) {
        Etiqueta e = etiquetaRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No existe: " + id));
        // delete closure entries
        arbolRepo.deleteAll(e.getAncestros());
        arbolRepo.deleteAll(e.getDescendientes());
        etiquetaRepo.delete(e);
    }

    @Transactional
    public void addRelation(Integer parentId, Integer childId) {
        if (parentId.equals(childId)) {
            throw new IllegalArgumentException("No self‐loops");
        }
        Etiqueta p = etiquetaRepo.findById(parentId)
            .orElseThrow(() -> new IllegalArgumentException("Padre no existe"));
        Etiqueta c = etiquetaRepo.findById(childId)
            .orElseThrow(() -> new IllegalArgumentException("Hijo no existe"));

        // direct parent→child distance=1
        arbolRepo.save(new ArbolEtiqueta(p, c, 1));
        // and for each ancestor A of p, we add A→c
        p.getAncestros().forEach(rel -> {
            arbolRepo.save(new ArbolEtiqueta(rel.getAncestro(), c, rel.getDistancia()+1));
        });
    }
}
