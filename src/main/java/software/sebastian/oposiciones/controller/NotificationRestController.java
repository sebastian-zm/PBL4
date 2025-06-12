package software.sebastian.oposiciones.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.sebastian.oposiciones.model.Notification;
import software.sebastian.oposiciones.model.Usuario;
import software.sebastian.oposiciones.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationRestController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/read/{notificationId}")
    public void markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
    }

    @GetMapping("/usuarioId")
    public Integer getUsuarioId(Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        return usuario.getUsuarioId();
    }  

    @GetMapping("/unread")
    public List<Notification> getUnreadNotifications(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal(); 
        return notificationService.getUnreadNotifications(usuario.getUsuarioId());
    }
}
