package software.sebastian.oposiciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.sebastian.oposiciones.model.Etiqueta;

public interface EtiquetaRepository extends JpaRepository<Etiqueta, Integer> { }
