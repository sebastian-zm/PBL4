package software.sebastian.oposiciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.sebastian.oposiciones.model.Convocatoria;

import java.util.Optional;

public interface ConvocatoriaRepository extends JpaRepository<Convocatoria, Integer> {
    Optional<Convocatoria> findByBoeId(String boeId);
}
