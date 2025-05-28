package software.sebastian.oposiciones.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import software.sebastian.oposiciones.model.Suscripcion;
import org.springframework.stereotype.Repository;
@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Integer> {
  List<Suscripcion> findByUsuarioId(Integer usuarioId);

}
