package software.sebastian.oposiciones.service;

import software.sebastian.oposiciones.model.Notification;
import software.sebastian.oposiciones.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepo;

    public Long createNotification(Integer usuarioId, String message) {
        Notification n = new Notification();
        n.setUsuarioId(usuarioId);
        n.setMessage(message);
        n.setRead(false);
        Notification nSaved = notificationRepo.save(n);

        return nSaved.getId();
    }

    public List<Notification> getUnreadNotifications(Integer usuarioId) {
        return notificationRepo.findByUsuarioIdAndIsReadFalse(usuarioId);
    }

    public void markAsRead(Long notificationId) {
        notificationRepo.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepo.save(n);
        });
    }

    public void deleteOldNotifications() {
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(30);
        notificationRepo.findAll().stream()
            .filter(n -> n.getCreatedAt().isBefore(expirationDate))
            .forEach(notificationRepo::delete);
    }
}
