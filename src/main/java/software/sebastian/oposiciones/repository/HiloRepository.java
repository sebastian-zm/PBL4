package software.sebastian.oposiciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.sebastian.oposiciones.model.Hilo;

public interface HiloRepository extends JpaRepository<Hilo, Integer> {}
