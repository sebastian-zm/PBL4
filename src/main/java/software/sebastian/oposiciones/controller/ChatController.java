package software.sebastian.oposiciones.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import software.sebastian.oposiciones.model.Hilo;
import software.sebastian.oposiciones.model.Mensaje;
import software.sebastian.oposiciones.model.MensajeDTO;
import software.sebastian.oposiciones.model.Usuario;
import software.sebastian.oposiciones.repository.HiloRepository;
import software.sebastian.oposiciones.repository.MensajeRepository;
import software.sebastian.oposiciones.repository.UsuarioRepository;

@Controller
public class ChatController {
    
    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private HiloRepository hiloRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public String chat() {
        return "chat/chat"; // Thymeleaf buscará templates/chat.html
    }

    @MessageMapping("/mensaje")
    @SendTo("/topic/mensajes")
    public MensajeDTO recibirMensaje(MensajeDTO mensajeDTO, Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Hilo hilo = hiloRepository.findById(mensajeDTO.getHiloId()).orElseThrow();

        Mensaje mensaje = new Mensaje();
        mensaje.setContenido(mensajeDTO.getContenido());
        mensaje.setHilo(hilo);
        mensaje.setUsuario(usuario);
        mensaje.setCreatedAt(LocalDateTime.now());

        mensajeRepository.save(mensaje);

        // Crear DTO para frontend
        MensajeDTO dto = new MensajeDTO();
        dto.setContenido(mensaje.getContenido());
        dto.setUsuarioNombre(usuario.getNombre());
        dto.setHiloId(hilo.getHiloId());
        dto.setCreatedAt(mensaje.getCreatedAt().toString()); // opcional

        return dto;
    }
}
