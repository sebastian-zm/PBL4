package software.sebastian.oposiciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.sebastian.oposiciones.model.Notification;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUsuarioIdAndIsReadFalse(Integer usuarioId);
    List<Notification> findByUsuarioId(Integer usuarioId);
}
