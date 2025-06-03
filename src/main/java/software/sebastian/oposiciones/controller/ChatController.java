package software.sebastian.oposiciones.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import software.sebastian.oposiciones.model.Hilo;
import software.sebastian.oposiciones.model.Mensaje;
import software.sebastian.oposiciones.model.MensajeDTO;
import software.sebastian.oposiciones.model.Usuario;
import software.sebastian.oposiciones.repository.HiloRepository;
import software.sebastian.oposiciones.repository.MensajeRepository;
import software.sebastian.oposiciones.repository.UsuarioRepository;
import software.sebastian.oposiciones.service.NotificationService;

@Controller
public class ChatController {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private HiloRepository hiloRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired    
    private NotificationService notiService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public String chat() {
        return "chat/chat";
    }

    @MessageMapping("/mensaje")
    @SendTo("/topic/mensajes")
    public MensajeDTO recibirMensaje(MensajeDTO mensajeDTO, Principal principal) {
        System.out.println("Mensaje recibido de: " + principal.getName());
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Hilo hilo = hiloRepository.findById(mensajeDTO.getHiloId())
                .orElseThrow(() -> new RuntimeException("Hilo no encontrado"));

        Mensaje mensaje = new Mensaje();
        mensaje.setContenido(mensajeDTO.getContenido());
        mensaje.setHilo(hilo);
        mensaje.setUsuario(usuario);
        mensaje.setCreatedAt(LocalDateTime.now());

        mensajeRepository.save(mensaje);

        Integer usuarioId = hilo.getCreador() != null ? hilo.getCreador().getUsuarioId() : null;
        System.out.println("UsuarioId para notificación: " + usuarioId);
        // Notificación
        if (usuarioId != null) {
            notiService.createNotification(usuarioId, usuario.getNombre() + " ha respondido a tu hilo: " + hilo.getTitulo());
        } else {
            // log o manejar error, para evitar excepción al guardar notificación sin usuario
            System.err.println("No se puede crear notificación, usuarioId es null");
        }

        if (!usuario.getUsuarioId().equals(hilo.getCreador().getUsuarioId())) {
            Map<String, Object> notiPayload = Map.of(
                "message", usuario.getNombre() + " ha respondido a tu hilo: " + hilo.getTitulo(),
                "id", mensajeDTO.getHiloId()
            );

            System.out.println("Enviando notificación a: " + hilo.getCreador().getEmail());
            messagingTemplate.convertAndSendToUser(
                hilo.getCreador().getEmail(), // email es el "username" del Principal
                "/queue/notificaciones",
                notiPayload
            );        
        }

        // Aquí es donde envías el mensaje por WebSocket a todos
        MensajeDTO dto = new MensajeDTO();
        dto.setContenido(mensaje.getContenido());
        dto.setUsuarioApodo(usuario.getApodo()); // 👈 cambio clave aquí
        dto.setHiloId(hilo.getHiloId());
        dto.setCreatedAt(mensaje.getCreatedAt().toString());

        return dto;
    }
}