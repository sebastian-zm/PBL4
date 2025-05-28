package software.sebastian.oposiciones.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.sebastian.oposiciones.model.Notification;
import software.sebastian.oposiciones.service.NotificationService;
import software.sebastian.oposiciones.model.Usuario;
import org.springframework.security.core.Authentication;

import java.util.List;

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
        Usuario usuario = (Usuario) authentication.getPrincipal(); // O usa usuarioService si prefieres
        return notificationService.getUnreadNotifications(usuario.getUsuarioId());
    }
}
