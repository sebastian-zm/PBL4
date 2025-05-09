package software.sebastian.oposiciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.sebastian.oposiciones.model.ArbolEtiqueta;
import software.sebastian.oposiciones.model.ArbolEtiquetaId;

public interface ArbolEtiquetaRepository extends 
        JpaRepository<ArbolEtiqueta, ArbolEtiquetaId> {
}
