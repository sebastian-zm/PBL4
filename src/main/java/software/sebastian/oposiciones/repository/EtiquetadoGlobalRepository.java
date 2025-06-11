// src/main/java/software/sebastian/oposiciones/repository/EtiquetadoRepository.java
package software.sebastian.oposiciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.sebastian.oposiciones.model.EtiquetadoGlobal;
import software.sebastian.oposiciones.model.EtiquetadoGlobal.PrimaryKey;

public interface EtiquetadoGlobalRepository extends JpaRepository<EtiquetadoGlobal, PrimaryKey> {
    // aquí puedes añadir métodos personalizados si los necesitas
}
