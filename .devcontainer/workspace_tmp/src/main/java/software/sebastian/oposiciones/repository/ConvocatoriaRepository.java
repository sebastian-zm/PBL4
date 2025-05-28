package software.sebastian.oposiciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.sebastian.oposiciones.model.Convocatoria;

public interface ConvocatoriaRepository extends JpaRepository<Convocatoria, Integer> {
    // podrías añadir métodos como findByBoeId(...) si los necesitas
}
